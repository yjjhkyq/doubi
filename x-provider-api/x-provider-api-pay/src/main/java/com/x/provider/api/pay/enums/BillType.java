package com.x.provider.api.pay.enums;

import lombok.Getter;

@Getter
public enum BillType {
    TRANSFER(1, "转账"),     // 转账
    RED_PACKET(2, "红包"),   // 红包
    PAY(3, "付款"),          // 支付
    REFUND(4, "退款"),       // 退款
    RECHARGE(5, "充值"),     // 充值
    WITHDRAW(6, "提现");     // 提现

    private int code;
    private String desc;

    BillType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
