package com.x.provider.customer.service.cache.customer;

import com.x.core.cache.event.EntityChanged;
import com.x.provider.customer.model.domain.CustomerPassword;

public class CustomerPasswordChangedEvent extends EntityChanged<CustomerPassword> {
    public CustomerPasswordChangedEvent(CustomerPassword entity) {
        super(entity);
    }
}
