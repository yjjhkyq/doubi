package com.x.provider.api.customer.service;

import com.x.core.web.api.R;
import com.x.provider.api.customer.constants.ServiceNameConstants;
import com.x.provider.api.customer.model.dto.IncCustomerStatRequestDTO;
import com.x.provider.api.customer.model.dto.ListCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.ListSimpleCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import com.x.provider.api.customer.service.factory.CustomerFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "customerService", value = ServiceNameConstants.CUSTOMER_SERVICE, fallbackFactory = CustomerFallbackFactory.class)
public interface CustomerRpcService {
    @GetMapping(ServiceNameConstants.CUSTOMER_URL_PREFIX + "/data")
    R<CustomerDTO> getCustomer(@RequestParam("customerId")long customerId, @RequestParam("customerOptions")List<String> customerOptions);

    @PostMapping(ServiceNameConstants.CUSTOMER_URL_PREFIX + "/list")
    R<Map<Long, CustomerDTO>> listCustomer(@RequestBody ListCustomerRequestDTO listCustomerAO);

    @PostMapping(ServiceNameConstants.CUSTOMER_URL_PREFIX + "/authorize")
    R<Long> authorize(@RequestParam("token") String token, @RequestParam("path") String path);

    @PostMapping(ServiceNameConstants.CUSTOMER_URL_PREFIX + "/follow/list")
    R<List<Long>> listFollow(@RequestParam("customerId") long customerId);

    @PostMapping(ServiceNameConstants.CUSTOMER_URL_PREFIX + "/simple/list")
    R<Map<Long, SimpleCustomerDTO>> listSimpleCustomer(@RequestBody ListSimpleCustomerRequestDTO listSimpleCustomer);

    @PostMapping(ServiceNameConstants.CUSTOMER_URL_PREFIX + "/stat/inc")
    R<Void> incCustomerStatAO(@RequestBody IncCustomerStatRequestDTO incCustomerStatAO);
}
