package com.x.provider.customer.service.cache.customer;

import com.x.core.cache.event.EntityChanged;
import com.x.provider.customer.model.domain.Customer;

public class CustomerChangedEvent extends EntityChanged<Customer> {
    public CustomerChangedEvent(Customer entity) {
        super(entity);
    }
}
