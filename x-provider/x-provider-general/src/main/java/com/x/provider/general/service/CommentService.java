package com.x.provider.general.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.provider.api.general.model.ao.ListCommentAO;
import com.x.provider.general.model.domain.Comment;

public interface CommentService {
    void comment(int itemType, long itemId, long commentCustomerId, String content);
    void commentReply(long commentId, long commentCustomerId, String content);
    void deleteComment(long id);
    IPage<Comment> listComment(ListCommentAO listCommentAO);
}
