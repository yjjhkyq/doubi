package com.x.provider.customer.service.cache.customer;

import com.x.core.cache.event.EntityChanged;
import com.x.provider.customer.model.domain.CustomerRole;

public class CustomerRoleChangedEvent extends EntityChanged<CustomerRole> {
    public CustomerRoleChangedEvent(CustomerRole entity) {
        super(entity);
    }
}
