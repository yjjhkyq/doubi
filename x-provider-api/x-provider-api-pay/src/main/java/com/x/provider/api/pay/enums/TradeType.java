package com.x.provider.api.pay.enums;

import com.x.core.enums.IntegerEnum;
import lombok.Getter;

@Getter
public enum TradeType implements IntegerEnum {
    TRANSFER(1),     // 转账
    RED_PACKET(2),   // 红包
    PAY(3),          // 支付
    REFUND(4),       // 退款
    RECHARGE(5),     // 充值
    WITHDRAW(6);     // 提现

    private int value;

    TradeType(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
