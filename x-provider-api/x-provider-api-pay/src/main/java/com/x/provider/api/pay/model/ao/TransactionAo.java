package com.x.provider.api.pay.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAo {

    private Integer transactionType;

    @Builder.Default
    private Long fromCustomerId = 0L;
    @Builder.Default
    private Long fromCoin = 0L;
    @Builder.Default
    private Long fromRice = 0L;
    @Builder.Default
    private Long fromCostCoin = 0L;

    @Builder.Default
    private Long toCustomerId = 0L;
    @Builder.Default
    private Long toCoin = 0L;
    @Builder.Default
    private Long toRice = 0L;
    @Builder.Default
    private Long toCostCoin = 0L;



    private OrderAO order;
}
