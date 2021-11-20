package com.x.provider.api.mc.enums;

import com.x.core.enums.IntegerEnum;

public enum SenderType implements IntegerEnum {
    FAN(1),
    INACTIVE(2),
    SYS_NOTIFY(3)
    ;

    private final Integer value;

    private SenderType(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return null;
    }
}
