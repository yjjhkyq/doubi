package com.x.provider.pay.model.bo.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResultBO {
    private String authorizationTransactionCode;
    private String orderNo;
}
