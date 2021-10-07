package com.x.provider.general.service;


import com.x.provider.api.general.model.event.StarRequestEvent;
import com.x.provider.general.model.domain.Star;

import java.util.List;

public interface StarService {
    boolean star(long associationItemId, long itemId, long starCustomerId, int itemType, boolean star);
    void onStarRequest(StarRequestEvent starRequestEvent);
    boolean isStarred(int itemType, long itemId, long customerId);
    List<Star> listStar(long associationItemId, long starCustomerId);
}
