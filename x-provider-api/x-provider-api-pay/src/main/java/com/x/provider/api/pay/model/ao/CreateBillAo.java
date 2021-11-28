package com.x.provider.api.pay.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBillAo {

    private Long customerId;

    private Long toCustomerId;

    private BigDecimal amount;

    private Integer billTypeCode;

    private Integer billStatusCode;

    private String comment;

}
