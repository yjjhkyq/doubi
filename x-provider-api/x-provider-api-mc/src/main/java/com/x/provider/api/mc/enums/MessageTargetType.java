package com.x.provider.api.mc.enums;

import com.x.core.enums.IntegerEnum;

public enum MessageTargetType implements IntegerEnum {
    PERSONAL(1),
    ALL(2),
    ;

    private final Integer value;

    MessageTargetType(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public static MessageTargetType valueOf(Integer value){
        for (MessageTargetType item : MessageTargetType.values()){
            if (item.getValue().equals(value)){
                return item;
            }
        }
        throw new IllegalStateException("not support, value:{}" + value);
    }
}
