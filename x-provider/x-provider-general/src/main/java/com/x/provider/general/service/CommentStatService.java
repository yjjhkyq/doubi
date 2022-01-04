package com.x.provider.general.service;

import com.x.provider.general.model.domain.Comment;
import com.x.provider.general.model.domain.CommentStatistic;
import com.x.provider.general.model.domain.Star;

import java.util.List;
import java.util.Map;

public interface CommentStatService {
    void onCommentInsert(Comment comment);
    void onStar(Star star);
    Map<Long, CommentStatistic> listCommentStatMap(List<Long> idList);
}
