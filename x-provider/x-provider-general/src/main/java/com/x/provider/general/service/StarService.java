package com.x.provider.general.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.provider.api.general.model.ao.ListStarAO;
import com.x.provider.api.general.model.ao.StarAO;
import com.x.provider.api.general.model.event.StarRequestEvent;
import com.x.provider.general.model.domain.Star;

import java.util.List;

public interface StarService {
    boolean star(long associationItemId, long itemId, long starCustomerId, int itemType, boolean star);
    void onStarRequest(StarRequestEvent starRequestEvent);
    void star(StarAO starAO);
    boolean isStarred(int itemType, long itemId, long customerId);
    IPage<Star> listStar(ListStarAO listStarAO);
}
