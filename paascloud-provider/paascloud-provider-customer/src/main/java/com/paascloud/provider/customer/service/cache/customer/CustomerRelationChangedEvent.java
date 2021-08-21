package com.paascloud.provider.customer.service.cache.customer;

import com.paascloud.core.cache.event.EntityChanged;
import com.paascloud.provider.customer.model.domain.Customer;
import com.paascloud.provider.customer.model.domain.CustomerRelation;

public class CustomerRelationChangedEvent extends EntityChanged<CustomerRelation> {
    public CustomerRelationChangedEvent(CustomerRelation entity) {
        super(entity);
    }
}
