package com.x.provider.api.pay.enums;

import com.x.core.enums.IntegerEnum;
import lombok.Getter;

@Getter
public enum OrderStatusEnum implements IntegerEnum {
    PENDING(10),
    PROCESSING(20),
    COMPLETE(30),
    CANCELED(40)
    ;

    private Integer value;

    OrderStatusEnum(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
