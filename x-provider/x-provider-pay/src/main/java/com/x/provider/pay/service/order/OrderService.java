package com.x.provider.pay.service.order;

import com.x.provider.api.pay.enums.PayMethodEnum;
import com.x.provider.api.pay.enums.PaymentStatusEnum;
import com.x.provider.api.pay.model.dto.CreateOrderDTO;
import com.x.provider.pay.model.domain.order.Order;
import com.x.provider.pay.model.domain.order.OrderItem;

import java.util.List;

public interface OrderService {
    String createOrderNo();
    Order createOrder(CreateOrderDTO createOrderAO);
    boolean pay(String orderNo, PayMethodEnum payMethodEnum, PaymentStatusEnum paymentStatusEnum);
    Order getOrder(String orderNo);
    boolean pay(Order order, PayMethodEnum payMethodEnum, PaymentStatusEnum paymentStatusEnum);
    List<OrderItem> listOrderItem(Long orderId);
}
