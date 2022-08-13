package com.x.provider.api.pay.enums;

import com.x.core.enums.IntegerEnum;

public enum ProductTypeEnum implements IntegerEnum {
    COIN(1),
    VIP(2)
    ;

    private Integer value;

    ProductTypeEnum(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
