package com.x.provider.general.service;

import com.x.core.web.page.PageList;
import com.x.provider.api.general.model.ao.CommentAO;
import com.x.provider.general.model.ao.ListCommentAO;
import com.x.provider.general.model.domain.Comment;

public interface CommentService {
    void comment(CommentAO commentAO);
    PageList<Comment> listComment(ListCommentAO listCommentAO);
    Comment getById(Long id);
}
