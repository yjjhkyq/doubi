package com.x.provider.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.provider.customer.mapper.CustomerAddressMapper;
import com.x.provider.customer.model.domain.CustomerAddress;
import com.x.provider.customer.model.query.CustomerAddressQuery;
import com.x.provider.customer.service.CustomerAddressService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerAddressServiceImpl implements CustomerAddressService {

    private final CustomerAddressMapper customerAddressMapper;

    public CustomerAddressServiceImpl(CustomerAddressMapper customerAddressMapper){
        this.customerAddressMapper = customerAddressMapper;
    }

    @Override
    public List<CustomerAddress> listCustomerAddress(Long customerId) {
        return list(CustomerAddressQuery.builder().customerId(customerId).build());
    }

    public List<CustomerAddress> list(CustomerAddressQuery query){
        return customerAddressMapper.selectList(buildQuery(query));
    }

    public LambdaQueryWrapper<CustomerAddress> buildQuery(CustomerAddressQuery customerAddressQuery){
        LambdaQueryWrapper<CustomerAddress> query = new LambdaQueryWrapper<>();
        if (customerAddressQuery.getCustomerId() != null){
            query = query.eq(CustomerAddress::getCustomerId, customerAddressQuery.getCustomerId());
        }
        return query;
    }
}
