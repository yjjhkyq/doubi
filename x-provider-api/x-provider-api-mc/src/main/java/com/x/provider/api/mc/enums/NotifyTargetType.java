package com.x.provider.api.mc.enums;

import com.x.core.enums.IntegerEnum;

public enum  NotifyTargetType implements IntegerEnum {
    PERSONAL(1),
    ALL(2),
    ;

    private final Integer value;

    private NotifyTargetType(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return null;
    }
}
