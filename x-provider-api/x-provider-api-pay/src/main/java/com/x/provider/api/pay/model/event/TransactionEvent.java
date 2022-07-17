package com.x.provider.api.pay.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private Long id;
    private Long fromCustomerId;
    private Long toCustomerId;
    private Long fromCoin;
    private Long fromRice;
    private Integer transactionType;
    private Long toCoin;
    private Long toRice;
    private Long orderId;
}
