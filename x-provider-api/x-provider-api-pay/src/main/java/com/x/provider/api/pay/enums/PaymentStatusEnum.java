package com.x.provider.api.pay.enums;

import com.x.core.enums.IntegerEnum;
import lombok.Getter;

@Getter
public enum PaymentStatusEnum implements IntegerEnum {
    PENDING(10),
    AUTHORIZED(20),
    PAID(30),
    PARTIALLY_REFUNDED(35),
    REFUNDED(40),
    VOIDED(50)
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
