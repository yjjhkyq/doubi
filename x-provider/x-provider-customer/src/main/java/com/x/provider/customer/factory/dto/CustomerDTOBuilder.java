package com.x.provider.customer.factory.dto;

import com.x.provider.api.customer.model.dto.ListCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.CustomerDTO;

import java.util.Map;

/**
 * @author: liushenyi
 * @date: 2022/07/25/16:04
 */
public interface CustomerDTOBuilder {
   void build(ListCustomerRequestDTO listCustomerAO, Map<Long, CustomerDTO> dest);
}
