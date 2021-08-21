package com.paascloud.provider.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paascloud.provider.customer.model.domain.Customer;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerMapper extends BaseMapper<Customer> {
}
