package com.paascloud.provider.customer.service.cache.customer;

import com.paascloud.core.cache.event.EntityChanged;
import com.paascloud.provider.customer.model.domain.Customer;

public class CustomerChangedEvent extends EntityChanged<Customer> {
    public CustomerChangedEvent(Customer entity) {
        super(entity);
    }
}
