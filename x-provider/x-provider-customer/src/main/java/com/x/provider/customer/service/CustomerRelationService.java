package com.x.provider.customer.service;

import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.customer.model.domain.CustomerRelation;

import java.util.List;

public interface CustomerRelationService {
    CustomerRelationEnum getRelation(long fromCustomerId, long toCustomerId);
    void following(long fromCustomerId, long toCustomerId);
    void unFollowing(long fromCustomerId, long toCustoemrId);
    List<CustomerRelation> listFollow(long customerId, long page, long limit);
    List<Long> listFollow(long customerId);
    List<CustomerRelation> listFans(long customerId, long page, long limit);
    long getFansCount(long customerId);
    long getFollowCount(long customerId);
}
