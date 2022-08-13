package com.x.provider.pay.service.payment;

import com.x.provider.api.pay.enums.PayMethodEnum;
import com.x.provider.pay.model.bo.payment.*;
import com.x.provider.pay.model.domain.order.Order;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface PaymentService {
    PayResultBO onPayNotify(PayNotifyBO payNotifyAO) throws GeneralSecurityException, IOException;
    CreateOrderResultBO createOrder(CreateOrderBO createOrderAO) throws IOException;
    QueryOrderResultBO queryOrder(PayMethodEnum payMethodEnum, String orderNo) throws IOException;
    void closeOrder(PayMethodEnum payMethodEnum, String orderNo) throws IOException;
    List<PayMethodEnum> listPayMethod();
}
