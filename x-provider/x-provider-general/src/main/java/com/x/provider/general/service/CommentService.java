package com.x.provider.general.service;

import com.x.core.web.page.PageList;
import com.x.provider.api.general.model.dto.CommentRequestDTO;
import com.x.provider.general.model.ao.ListCommentAO;
import com.x.provider.general.model.domain.Comment;

public interface CommentService {
    void comment(CommentRequestDTO commentAO);
    PageList<Comment> listComment(ListCommentAO listCommentAO);
    Comment getById(Long id);
}
