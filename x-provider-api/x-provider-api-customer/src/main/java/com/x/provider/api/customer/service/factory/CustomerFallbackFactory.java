package com.x.provider.api.customer.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.customer.model.dto.IncCustomerStatRequestDTO;
import com.x.provider.api.customer.model.dto.ListCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.ListSimpleCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomerFallbackFactory implements FallbackFactory<CustomerRpcService> {
    @Override
    public CustomerRpcService create(Throwable throwable) {
        return new CustomerRpcService() {
            @Override
            public R<CustomerDTO> getCustomer(long customerId, List<String> customerOptions) {
                return null;
            }

            @Override
            public R<Map<Long, CustomerDTO>> listCustomer(ListCustomerRequestDTO listCustomerAO) {
                return null;
            }


            @Override
            public R<Long> authorize(String token, String path) {
                return null;
            }

            @Override
            public R<List<Long>> listFollow(long customerId) {
                return R.ok(new ArrayList<>());
            }

            @Override
            public R<Map<Long, SimpleCustomerDTO>> listSimpleCustomer(ListSimpleCustomerRequestDTO listSimpleCustomer) {
                return R.ok(new HashMap<>());
            }

            @Override
            public R<Void> incCustomerStatAO(IncCustomerStatRequestDTO incCustomerStatAO) {
                return R.ok();
            }
        };
    }
}
