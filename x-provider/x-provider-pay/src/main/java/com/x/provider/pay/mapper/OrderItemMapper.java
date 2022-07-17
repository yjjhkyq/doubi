package com.x.provider.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.provider.pay.model.domain.Order;
import com.x.provider.pay.model.domain.OrderItem;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
