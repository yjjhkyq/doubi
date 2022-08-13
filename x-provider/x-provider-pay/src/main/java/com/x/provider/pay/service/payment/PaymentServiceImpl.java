package com.x.provider.pay.service.payment;

import com.x.core.utils.SpringUtils;
import com.x.provider.api.pay.enums.PayMethodEnum;
import com.x.provider.pay.model.bo.payment.*;
import com.x.provider.pay.model.domain.order.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Map<PayMethodEnum, PaymentMethod> PAY_RPOVIDER = SpringUtils.getBeansOfType(PaymentMethod.class).values().stream().collect(Collectors.toMap(item -> item.getPayMethod(), item -> item));

    @Override
    public PayResultBO onPayNotify(PayNotifyBO payNotifyAO) throws GeneralSecurityException, IOException {
        return getPayProvider(payNotifyAO.getPayMethod()).onPayNotify(payNotifyAO);
    }

    @Override
    public CreateOrderResultBO createOrder(CreateOrderBO createOrderAO) throws IOException {
        return getPayProvider(createOrderAO.getPayMethodEnum()).createOrder(createOrderAO.getOrder());
    }

    @Override
    public QueryOrderResultBO queryOrder(PayMethodEnum payMethodEnum, String orderNo) throws IOException {
        return getPayProvider(payMethodEnum).queryOrder(orderNo);
    }

    @Override
    public void closeOrder(PayMethodEnum payMethodEnum, String orderNo) throws IOException {
        getPayProvider(payMethodEnum).closeOrder(orderNo);
    }

    @Override
    public List<PayMethodEnum> listPayMethod(){
        return PAY_RPOVIDER.keySet().stream().collect(Collectors.toList());
    }

    private PaymentMethod getPayProvider(PayMethodEnum payMethodEnum){
        return PAY_RPOVIDER.get(payMethodEnum);
    }
}
