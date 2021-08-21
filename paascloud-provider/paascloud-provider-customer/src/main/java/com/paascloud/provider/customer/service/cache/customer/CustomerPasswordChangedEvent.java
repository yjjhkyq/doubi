package com.paascloud.provider.customer.service.cache.customer;

import com.paascloud.core.cache.event.EntityChanged;
import com.paascloud.provider.customer.model.domain.CustomerPassword;

public class CustomerPasswordChangedEvent extends EntityChanged<CustomerPassword> {
    public CustomerPasswordChangedEvent(CustomerPassword entity) {
        super(entity);
    }
}
