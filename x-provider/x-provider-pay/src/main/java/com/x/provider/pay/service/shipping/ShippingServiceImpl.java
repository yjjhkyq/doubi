package com.x.provider.pay.service.shipping;

import com.x.core.utils.SpringUtils;
import com.x.provider.api.pay.enums.ProductTypeEnum;
import com.x.provider.pay.model.domain.order.Order;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShippingServiceImpl implements ShippingService{
    private static final Map<ProductTypeEnum, ShippingMethod> SHIPPING_METHOD_MAP = SpringUtils.getBanListOfType(ShippingMethod.class).stream()
            .collect(Collectors.toMap(item -> item.getProductType(), item -> item));
    @Override
    public void shipping(Order order) {
        SHIPPING_METHOD_MAP.get(order.getProductType()).shipping(order);
    }

}
