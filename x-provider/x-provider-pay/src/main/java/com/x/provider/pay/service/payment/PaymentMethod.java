package com.x.provider.pay.service.payment;

import com.x.provider.api.pay.enums.PayMethodEnum;
import com.x.provider.pay.model.bo.payment.CreateOrderResultBO;
import com.x.provider.pay.model.bo.payment.PayNotifyBO;
import com.x.provider.pay.model.bo.payment.PayResultBO;
import com.x.provider.pay.model.bo.payment.QueryOrderResultBO;
import com.x.provider.pay.model.domain.order.Order;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface PaymentMethod {
    PayResultBO onPayNotify(PayNotifyBO payNotifyAO) throws GeneralSecurityException, IOException;
    CreateOrderResultBO createOrder(Order order) throws IOException;
    QueryOrderResultBO queryOrder(String orderNo) throws IOException;
    void closeOrder(String orderNo) throws IOException;
    PayMethodEnum getPayMethod();
}
