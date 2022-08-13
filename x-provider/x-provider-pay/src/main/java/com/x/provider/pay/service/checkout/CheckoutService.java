package com.x.provider.pay.service.checkout;

import com.x.provider.api.pay.model.dto.CreateTransactionDTO;
import com.x.provider.api.pay.model.dto.TransactionDTO;
import com.x.provider.pay.model.bo.payment.PayResultBO;

public interface CheckoutService {
    TransactionDTO transaction(CreateTransactionDTO transaction);
    void pay(PayResultBO payResult);
}
