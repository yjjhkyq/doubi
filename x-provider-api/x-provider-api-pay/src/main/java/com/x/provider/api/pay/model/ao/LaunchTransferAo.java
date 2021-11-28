package com.x.provider.api.pay.model.ao;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LaunchTransferAo {

    private BigDecimal amount;
    private Long toCustomerId;
    private String comment;

}
