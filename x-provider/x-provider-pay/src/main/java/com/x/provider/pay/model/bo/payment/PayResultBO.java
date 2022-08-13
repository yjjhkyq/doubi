package com.x.provider.pay.model.bo.payment;

import com.x.provider.api.pay.enums.PayMethodEnum;
import com.x.provider.api.pay.enums.PaymentStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayResultBO {
    private String orderNo;
    private PayMethodEnum payMethod;
    private PaymentStatusEnum paymentStatus;
    private Boolean success;
}
