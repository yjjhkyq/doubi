package com.x.provider.customer.factory.dto;

import com.x.provider.api.customer.model.dto.ListCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.ListSimpleCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;

import java.util.Map;

/**
 * @author: liushenyi
 * @date: 2022/07/25/16:04
 */
public interface CustomerFactory {
    Map<Long, CustomerDTO> prepare(ListCustomerRequestDTO listCustomerAO);
    Map<Long, SimpleCustomerDTO> prepare(ListSimpleCustomerRequestDTO listSimpleCustomerAO);
}
