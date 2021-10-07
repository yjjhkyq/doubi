package com.x.provider.general.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.web.api.R;
import com.x.core.web.page.TableSupport;
import com.x.provider.api.general.enums.CommentItemTypeEnum;
import com.x.provider.api.general.model.ao.ListCommentAO;
import com.x.provider.api.oss.enums.GreenDataTypeEnum;
import com.x.provider.api.oss.enums.SuggestionTypeEnum;
import com.x.provider.api.oss.model.ao.GreenRpcAO;
import com.x.provider.api.oss.service.GreenRpcService;
import com.x.provider.api.statistic.constants.StatisticEventTopic;
import com.x.provider.api.statistic.model.event.StatisticTotalEvent;
import com.x.provider.general.enums.CommentStatisticEnum;
import com.x.provider.general.enums.GeneralErrorEnum;
import com.x.provider.general.mapper.CommentMapper;
import com.x.provider.general.model.domain.Comment;
import com.x.provider.general.service.CommentService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final GreenRpcService greenRpcService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CommentServiceImpl(CommentMapper commentMapper,
                              GreenRpcService greenRpcService,
                              KafkaTemplate<String, Object> kafkaTemplate){
        this.commentMapper = commentMapper;
        this.greenRpcService = greenRpcService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void comment(int itemType, long itemId, long commentCustomerId, String content) {
        R<String> greenResult = greenRpcService.greenSync(GreenRpcAO.builder().dataType(GreenDataTypeEnum.TEXT.name()).value(content).build());
        ApiAssetUtil.isTrue(SuggestionTypeEnum.PASS.name().equals(greenResult.getData()), GeneralErrorEnum.COMMENT_REVIEW_BLOCKED);
        Comment comment = Comment.builder().itemType(itemType).itemId(itemId).commentCustomerId(commentCustomerId).content(content).build();
        commentMapper.insert(comment);
        List<CommentStatisticEnum> commentStatistics = CommentStatisticEnum.valueOf(itemType, false);
        commentStatistics.forEach(item -> {
            kafkaTemplate.send(StatisticEventTopic.TOPIC_NAME_STAT_TOTAL_EVENT, StrUtil.format("{}:{}", itemType, itemId), StatisticTotalEvent.builder().statTotalItemNameEnum(item.getStatTotalItemName().getValue())
                    .statisticPeriodEnum(item.getStatisticPeriod().getValue()).statisticObjectId(String.valueOf(itemId)).longValue(1L).statisticObjectClassEnum(item.getStatisticObjectClass().getValue()).build());
        });

    }

    @Override
    public void commentReply(long commentId, long commentCustomerId, String content) {
        Comment comment = getCommentById(commentId);
        if (comment == null){
            return;
        }
        R<String> greenResult = greenRpcService.greenSync(GreenRpcAO.builder().dataType(GreenDataTypeEnum.TEXT.name()).value(content).build());
        ApiAssetUtil.isTrue(SuggestionTypeEnum.PASS.name().equals(greenResult.getData()), GeneralErrorEnum.COMMENT_REVIEW_BLOCKED);
        Comment commentReply = Comment.builder().replyCommentId(commentId).itemType(comment.getItemType()).itemId(comment.getItemId()).commentCustomerId(commentCustomerId).content(content)
                .replyRootId(comment.getReplyRootId() == 0 ? comment.getId() : comment.getReplyRootId()).build();
        commentMapper.insert(commentReply);
        CommentStatisticEnum.valueOf(comment.getItemType(), false).forEach(item -> {
            kafkaTemplate.send(StatisticEventTopic.TOPIC_NAME_STAT_TOTAL_EVENT, StrUtil.format("{}:{}", comment.getItemType(), comment.getItemId()), StatisticTotalEvent.builder().statTotalItemNameEnum(item.getStatTotalItemName().getValue())
                    .statisticPeriodEnum(item.getStatisticPeriod().getValue()).statisticObjectId(String.valueOf(comment.getItemId())).longValue(1L).statisticObjectClassEnum(item.getStatisticObjectClass().getValue()).build());
        });
        CommentStatisticEnum.valueOf(CommentItemTypeEnum.COMMENT.getValue(), true).forEach(item -> {
            kafkaTemplate.send(StatisticEventTopic.TOPIC_NAME_STAT_TOTAL_EVENT, StrUtil.format("{}:{}", comment.getItemType(), comment.getItemId()), StatisticTotalEvent.builder().statTotalItemNameEnum(item.getStatTotalItemName().getValue())
                    .statisticPeriodEnum(item.getStatisticPeriod().getValue()).statisticObjectId(String.valueOf(comment.getItemId())).longValue(1L).statisticObjectClassEnum(item.getStatisticObjectClass().getValue()).build());
        });
    }

    @Override
    public void deleteComment(long id) {
        Comment comment = getCommentById(id);
        if (comment == null){
            return;
        }
        commentMapper.deleteById(id);
        CommentStatisticEnum.valueOf(comment.getItemType(), false).forEach(item -> {
            kafkaTemplate.send(StatisticEventTopic.TOPIC_NAME_STAT_TOTAL_EVENT, StrUtil.format("{}:{}", comment.getItemType(), comment.getItemId()), StatisticTotalEvent.builder().statTotalItemNameEnum(item.getStatTotalItemName().getValue())
                    .statisticPeriodEnum(item.getStatisticPeriod().getValue()).statisticObjectId(String.valueOf(comment.getItemId())).longValue(-1L).statisticObjectClassEnum(item.getStatisticObjectClass().getValue()).build());
        });
        if (comment.getReplyCommentId() > 0) {
            CommentStatisticEnum.valueOf(comment.getItemType(), true).forEach(item -> {
                kafkaTemplate.send(StatisticEventTopic.TOPIC_NAME_STAT_TOTAL_EVENT, StrUtil.format("{}:{}", comment.getItemType(), comment.getItemId()), StatisticTotalEvent.builder().statTotalItemNameEnum(item.getStatTotalItemName().getValue())
                        .statisticPeriodEnum(item.getStatisticPeriod().getValue()).statisticObjectId(String.valueOf(comment.getItemId())).longValue(-1L).statisticObjectClassEnum(item.getStatisticObjectClass().getValue()).build());
            });
        }
    }

    @Override
    public IPage<Comment> listComment(ListCommentAO listCommentAO) {
        return commentMapper.selectPage(TableSupport.buildIPageRequest(listCommentAO), build(listCommentAO.getItemId(), listCommentAO.getItemType(), listCommentAO.getCommentCustomerId(), listCommentAO.getReplyCommentId(),
                listCommentAO.getReplyRootId()).orderByAsc(Comment::getId));
    }

    LambdaQueryWrapper<Comment> build(long itemId, int itemType, long commentCustomerId, long replyCommentId, long replyRootId){
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
        if (replyCommentId > 0){
            query = query.eq(Comment::getReplyCommentId, replyCommentId);
        }
        if (replyRootId > 0){
            query = query.eq(Comment::getReplyRootId, replyRootId);
        }
        return query;
    }
    private Comment getCommentById(long id){
        return commentMapper.selectById(id);
    }


}
