package com.x.provider.api.pay.model.ao;

import com.x.provider.api.pay.enums.TradeType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceChangeAo {

    private long toCustomerId;

    private TradeType type;

    private BigDecimal amount;

    private String comment;  // 备注
}
