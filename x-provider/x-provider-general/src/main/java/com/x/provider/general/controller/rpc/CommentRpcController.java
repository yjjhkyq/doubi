package com.x.provider.general.controller.rpc;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.page.TableDataInfo;
import com.x.core.web.page.TableSupport;
import com.x.provider.api.general.model.ao.*;
import com.x.provider.api.general.model.dto.CommentDTO;
import com.x.provider.api.general.model.dto.StarDTO;
import com.x.provider.api.general.service.CommentRpcService;
import com.x.provider.api.general.service.StarRpcService;
import com.x.provider.general.model.domain.Comment;
import com.x.provider.general.service.CommentService;
import com.x.provider.general.service.StarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rpc/general/comment")
public class CommentRpcController implements CommentRpcService {

    private final CommentService commentService;

    public CommentRpcController(CommentService commentService){
        this.commentService = commentService;
    }

    @PostMapping("insert")
    @Override
    public R<Void> comment(@RequestBody CommentAO commentAO) {
        commentService.comment(commentAO.getItemType(), commentAO.getItemId(), commentAO.getCommentCustomerId(), commentAO.getContent());
        return R.ok();
    }

    @PostMapping("reply")
    @Override
    public R<Void> commentReply(@RequestBody CommentReplyAO commentReplyAO) {
        commentService.commentReply(commentReplyAO.getCommentId(), commentReplyAO.getCommentCustomerId(), commentReplyAO.getContent());
        return null;
    }

    @PostMapping("list")
    @Override
    public R<TableDataInfo<CommentDTO>> listComment(@RequestBody ListCommentAO listCommentAO) {
        IPage<Comment> comments = commentService.listComment(listCommentAO);
        return R.ok(TableSupport.buildTableDataInfo(comments,(item) -> BeanUtil.prepare(item, CommentDTO.class)));
    }
}
