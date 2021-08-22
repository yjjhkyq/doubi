package com.x.provider.customer.service.cache.customer;

import com.x.core.cache.event.EntityChanged;
import com.x.provider.customer.model.domain.CustomerRelation;

public class CustomerRelationChangedEvent extends EntityChanged<CustomerRelation> {
    public CustomerRelationChangedEvent(CustomerRelation entity) {
        super(entity);
    }
}
