package com.x.provider.pay.enums.bill;

import com.x.provider.pay.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum BillType implements BaseEnum {
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

    public static BillType getEnum(int billTypeCode) {
        return (BillType) BaseEnum.getEnum(BillType.class, billTypeCode);
    }
}
