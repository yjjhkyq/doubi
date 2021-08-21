package com.paascloud.provider.customer.service;

import com.paascloud.provider.customer.enums.CustomerRelationEnum;
import com.paascloud.provider.customer.model.domain.CustomerRelation;

import java.util.List;

public interface CustomerRelationService {
    CustomerRelationEnum getRelation(long fromCustomerId, long toCustomerId);
    void following(long fromCustomerId, long toCustomerId);
    void unFollowing(long fromCustomerId, long toCustoemrId);
    List<CustomerRelation> listFollow(long customerId, long page, long limit);
    List<CustomerRelation> listFans(long customerId, long page, long limit);
    long getFansCount(long customerId);
    long getFollowCount(long customerId);
}
