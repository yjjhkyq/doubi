package com.paascloud.provider.customer.service.cache.customer;

import com.paascloud.core.cache.event.EntityChanged;
import com.paascloud.provider.customer.model.domain.CustomerRole;

public class CustomerRoleChangedEvent extends EntityChanged<CustomerRole> {
    public CustomerRoleChangedEvent(CustomerRole entity) {
        super(entity);
    }
}
