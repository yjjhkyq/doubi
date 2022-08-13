package com.x.provider.pay.factory.vo;

import com.x.provider.api.pay.enums.OrderStatusEnum;
import com.x.provider.api.pay.enums.PayMethodEnum;
import com.x.provider.api.pay.enums.PaymentStatusEnum;
import com.x.provider.api.pay.model.dto.CreateOrderDTO;
import com.x.provider.api.pay.model.dto.CreateOrderItemDTO;
import com.x.provider.pay.model.domain.product.Product;
import com.x.provider.pay.model.vo.CreateOrderVO;
import com.x.provider.pay.service.product.ProductService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PayVOFactoryImpl implements PayVOFactory{

    private final ProductService<Product> productService;

    public PayVOFactoryImpl(ProductService<Product> productService){
        this.productService = productService;
    }

    @Override
    public CreateOrderDTO prepare(CreateOrderVO createOrderVO, Long sessionCustomerId) {
        Product product = productService.getById(createOrderVO.getProductId());
        PayMethodEnum payMethodEnum = PayMethodEnum.valueOf(createOrderVO.getPayMethod());
        CreateOrderDTO result = CreateOrderDTO.builder()
                .customerId(sessionCustomerId)
                .orderStatus(OrderStatusEnum.PENDING.getValue())
                .paymentStatus(PaymentStatusEnum.NOTPAY.getValue())
                .orderTotal(product.getPrice())
                .payMethodId(createOrderVO.getPayMethod())
                .productType(product.getProductType())
                .build();
        CreateOrderItemDTO createOrderItemDTO = CreateOrderItemDTO.builder()
                .customerId(sessionCustomerId)
                .originalProductCost(product.getPrice())
                .payProductCost(product.getPrice())
                .productId(product.getId())
                .quantity(1L)
                .productType(product.getProductType())
                .build();
        result.setOrderItemList(Arrays.asList(createOrderItemDTO));
        return result;
    }
}
