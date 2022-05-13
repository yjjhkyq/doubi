package com.x.provider.general.service;


import com.x.provider.api.general.model.ao.ListStarAO;
import com.x.provider.api.general.model.ao.StarAO;
import com.x.provider.general.model.domain.Star;

import java.util.List;

public interface StarService {
    boolean star(StarAO starAO);
    boolean isStarred(int itemType, long itemId, long customerId);
    List<Star> listStar(ListStarAO listStarAO);
}
