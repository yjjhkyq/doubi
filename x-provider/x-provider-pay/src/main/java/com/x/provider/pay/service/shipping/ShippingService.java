package com.x.provider.pay.service.shipping;

import com.x.provider.pay.model.domain.order.Order;

public interface ShippingService {
    void shipping(Order order);
}
