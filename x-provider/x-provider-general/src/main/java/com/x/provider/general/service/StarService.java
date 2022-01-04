package com.x.provider.general.service;


import com.x.provider.api.general.model.ao.StarAO;

public interface StarService {
    boolean star(StarAO starAO);
    boolean isStarred(int itemType, long itemId, long customerId);
}
