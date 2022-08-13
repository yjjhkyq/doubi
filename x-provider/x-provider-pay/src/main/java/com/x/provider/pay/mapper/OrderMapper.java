package com.x.provider.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.provider.pay.model.domain.order.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMapper extends BaseMapper<Order> {
}
