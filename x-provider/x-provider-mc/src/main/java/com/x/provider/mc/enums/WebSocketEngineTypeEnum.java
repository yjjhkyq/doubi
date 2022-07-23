package com.x.provider.mc.enums;

import com.x.core.enums.IntegerEnum;

public enum WebSocketEngineTypeEnum implements IntegerEnum {
    CENTRIFUGO(1),
    X(2)
    ;

    private final Integer value;

    WebSocketEngineTypeEnum(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
