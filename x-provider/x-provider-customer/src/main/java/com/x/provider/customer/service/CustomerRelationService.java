package com.x.provider.customer.service;

import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.customer.model.domain.CustomerRelation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CustomerRelationService {
    CustomerRelationEnum getRelation(long fromCustomerId, long toCustomerId);
    void following(long fromCustomerId, long toCustomerId);
    void unFollowing(long fromCustomerId, long toCustoemrId);
    PageList<CustomerRelation> listFollow(long customerId, PageDomain page);
    List<Long> listFollow(long customerId);
    PageList<CustomerRelation> listFans(long customerId, PageDomain page);
    long getFansCount(long customerId);
    long getFollowCount(long customerId);
    Map<Long, CustomerRelation> listRelationMap(long customerId, CustomerRelationEnum customerRelation, List<Long> toCustomerIdList);
    Set<Long> listFollowCustomer(long fromCustomerId, List<Long> toCustomerIdList);
}
