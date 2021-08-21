package com.paascloud.provider.api.customer.service;

import com.paascloud.core.web.api.R;
import com.paascloud.provider.api.customer.constants.ServiceNameConstants;
import com.paascloud.provider.api.customer.model.dto.CustomerDTO;
import com.paascloud.provider.api.customer.service.factory.CustomerFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "customerService", value = ServiceNameConstants.CUSTOMER_SERVICE, fallbackFactory = CustomerFallbackFactory.class)
public interface CustomerRpcService {
    @GetMapping(ServiceNameConstants.CUSTOMER_URL_PREFIX + "/getCustomer")
    R<CustomerDTO> getCustomer(@RequestParam("customerId")long customerId, @RequestParam("customerOptions")List<String> customerOptions);

    @PostMapping(ServiceNameConstants.CUSTOMER_URL_PREFIX + "/authorize")
    R<Long> authorize(@RequestParam("token") String token, @RequestParam("path") String path);

}
