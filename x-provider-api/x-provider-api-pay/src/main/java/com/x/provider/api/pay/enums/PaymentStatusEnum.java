package com.x.provider.api.pay.enums;

import com.x.core.enums.IntegerEnum;
import lombok.Getter;

@Getter
public enum PaymentStatusEnum implements IntegerEnum {
    NOTPAY(10),
    REVOKED(20),
    SUCCESS(30),
    USERPAYING(35),
    REFUNDED(40),
    CLOSED(50),
    PAYERROR(60)
    ;

    private Integer value;

    PaymentStatusEnum(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
