package com.x.provider.api.customer.enums;

import com.x.core.enums.IntegerEnum;

public enum GenderEnum implements IntegerEnum {
    MALE(1),
    FEMALE(2)
    ;

    private final Integer value;

    GenderEnum(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
