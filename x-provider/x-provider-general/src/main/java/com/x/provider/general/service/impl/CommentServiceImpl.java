package com.x.provider.general.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.page.PageList;
import com.x.provider.api.general.constants.GeneralEventTopic;
import com.x.provider.api.general.model.ao.CommentAO;
import com.x.provider.api.general.model.event.CommentEvent;
import com.x.provider.api.oss.enums.GreenDataTypeEnum;
import com.x.provider.api.oss.enums.SuggestionTypeEnum;
import com.x.provider.api.oss.model.ao.GreenRpcAO;
import com.x.provider.api.oss.service.GreenRpcService;
import com.x.provider.general.enums.GeneralErrorEnum;
import com.x.provider.general.mapper.CommentMapper;
import com.x.provider.general.model.ao.ListCommentAO;
import com.x.provider.general.model.domain.Comment;
import com.x.provider.general.service.CommentService;
import com.x.provider.general.service.CommentStatService;
import com.x.provider.general.service.ItemStatService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final GreenRpcService greenRpcService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CommentStatService commentStatService;
    private final ItemStatService itemStatService;

    public CommentServiceImpl(CommentMapper commentMapper,
                              GreenRpcService greenRpcService,
                              KafkaTemplate<String, Object> kafkaTemplate,
                              CommentStatService commentStatService,
                              ItemStatService itemStatService){
        this.commentMapper = commentMapper;
        this.greenRpcService = greenRpcService;
        this.kafkaTemplate = kafkaTemplate;
        this.commentStatService = commentStatService;
        this.itemStatService = itemStatService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void comment(CommentAO commentAO) {
        R<String> greenResult = greenRpcService.greenSync(GreenRpcAO.builder().dataType(GreenDataTypeEnum.TEXT.name()).value(commentAO.getContent()).build());
        ApiAssetUtil.isTrue(SuggestionTypeEnum.PASS.name().equals(greenResult.getData()), GeneralErrorEnum.COMMENT_REVIEW_BLOCKED);
        Comment comment = BeanUtil.prepare(commentAO, Comment.class);
        if (commentAO.getParentCommentId() > 0){
            Comment parentComment = getCommentById(commentAO.getParentCommentId());
            comment.setRootCommentId(parentComment.getRootCommentId());
            comment.setItemId(parentComment.getItemId());
            comment.setItemType(parentComment.getItemType());
            comment.setRootCommentId(parentComment.getRootCommentId() > 0 ? parentComment.getRootCommentId() : parentComment.getId());
            comment.setParentCommentCustomerId(parentComment.getCommentCustomerId());

        }
        comment.setAuthorComment(comment.getItemCustomerId().equals(comment.getCommentCustomerId()));
        commentMapper.insert(comment);
        commentStatService.onCommentInsert(comment);
        itemStatService.onCommentInsert(comment);
        publishCommentEvent(comment);
    }


    @Override
    public PageList<Comment> listComment(ListCommentAO listCommentAO) {
        LambdaQueryWrapper<Comment> query = buildQuery(listCommentAO.getItemId(), listCommentAO.getItemType(), 0L, listCommentAO.getRootCommentId()).orderByDesc(Comment::getId)
                .last(StrUtil.format(" limit {} ", listCommentAO.getPageSize()));
        if (listCommentAO.getCursor() > 0){
            query.lt(Comment::getId, listCommentAO.getCursor());
        }
        List<Comment> comments = commentMapper.selectList(query);
        if (comments.isEmpty()){
            return new PageList<>();
        }
        return new PageList<>(comments, listCommentAO.getPageSize(), CollectionUtils.lastElement(comments).getId());
    }

    @Override
    public Comment getById(Long id) {
        return commentMapper.selectById(id);
    }

    private LambdaQueryWrapper<Comment> buildQuery(long itemId, int itemType, long commentCustomerId, Long rootCommentId){
        var query = new LambdaQueryWrapper<Comment>();
        if (itemId >0){
            query = query.eq(Comment::getItemId, itemId);

        }
        if (itemType > 0){
            query = query.eq(Comment::getItemType, itemType);
        }

        if (commentCustomerId > 0){
            query = query.eq(Comment::getCommentCustomerId, commentCustomerId);
        }

        if (rootCommentId != null){
            query = query.eq(Comment::getRootCommentId, rootCommentId);
        }
        return query;
    }
    private Comment getCommentById(long id){
        return commentMapper.selectById(id);
    }

    private void publishCommentEvent(Comment comment){
        CommentEvent commentEvent = BeanUtil.prepare(comment, CommentEvent.class);
        kafkaTemplate.send(GeneralEventTopic.TOPIC_NAME_COMMENT, StrUtil.format("{}:{}", comment.getItemId(), comment.getItemType()), commentEvent);
    }

}
