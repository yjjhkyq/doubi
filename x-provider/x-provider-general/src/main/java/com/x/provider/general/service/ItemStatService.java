package com.x.provider.general.service;

import com.x.provider.general.model.domain.Comment;
import com.x.provider.general.model.domain.ItemStatistic;
import com.x.provider.general.model.domain.Star;

import java.util.List;
import java.util.Map;

public interface ItemStatService {
    void onCommentInsert(Comment comment);
    void onStar(Star star);
    Map<Long, ItemStatistic> listItemStatMap(int itemType, List<Long> idList);
}
