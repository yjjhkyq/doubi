package com.x.provider.customer.service;

import com.x.provider.api.customer.model.event.CustomerInfoGreenEvent;
import com.x.provider.api.customer.model.event.FollowEvent;

public interface CustomerMcService {
    void onCustomerInfoGreen(CustomerInfoGreenEvent event);
    void onFollowEvent(FollowEvent followEvent);
}
