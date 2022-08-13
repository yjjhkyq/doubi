package com.x.provider.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.provider.customer.model.domain.CustomerAddress;
import com.x.provider.customer.model.domain.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerAddressMapper extends BaseMapper<CustomerAddress> {
}
