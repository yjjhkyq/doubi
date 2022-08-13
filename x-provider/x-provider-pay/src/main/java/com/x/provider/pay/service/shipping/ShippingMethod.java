package com.x.provider.pay.service.shipping;

import com.x.provider.api.pay.enums.ProductTypeEnum;
import com.x.provider.pay.model.domain.order.Order;

public interface ShippingMethod {
    void shipping(Order order);
    ProductTypeEnum getProductType();
}
