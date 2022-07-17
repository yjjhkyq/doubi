package com.x.provider.customer.service;

import com.x.provider.api.customer.model.event.FollowEvent;
import com.x.provider.customer.model.domain.CustomerStat;

import java.util.List;
import java.util.Map;

public interface CustomerStatService {
    void inc(CustomerStat customerStat);
     Map<Long, CustomerStat> list(List<Long> idList);
     void onFollowEvent(FollowEvent followEvent);
     CustomerStat getCustomerStat(Long customerId);
}
