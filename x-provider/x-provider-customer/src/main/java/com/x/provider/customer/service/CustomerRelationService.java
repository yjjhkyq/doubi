package com.x.provider.customer.service;

import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.customer.model.domain.CustomerRelation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CustomerRelationService {
    CustomerRelation getRelation(Long fromCustomerId, Long toCustomerId);
    void following(Long fromCustomerId, Long toCustomerId);
    void unFollowing(Long fromCustomerId, Long toCustomerId);
    PageList<CustomerRelation> listFollow(Long customerId, PageDomain page);
    List<Long> listFollow(Long customerId);
    PageList<CustomerRelation> listFans(Long customerId, PageDomain page);
    Map<Long, CustomerRelation> listRelationMap(Long customerId, List<Long> toCustomerIdList, CustomerRelationEnum relationEnum);
    Set<Long> listFollowCustomer(Long fromCustomerId, List<Long> toCustomerIdList);
}
