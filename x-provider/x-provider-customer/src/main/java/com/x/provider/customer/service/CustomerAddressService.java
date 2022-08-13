package com.x.provider.customer.service;

import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.customer.model.domain.CustomerAddress;
import com.x.provider.customer.model.domain.CustomerRelation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CustomerAddressService {
    List<CustomerAddress> listCustomerAddress(Long customerId);
}
