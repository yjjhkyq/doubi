package com.x.provider.api.mc.enums;

import com.x.core.enums.IntegerEnum;

public enum ConversationType implements IntegerEnum {
    C2C(1),
    GROUP(2),
    ;

    private final Integer value;

    ConversationType(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public static ConversationType valueOf(Integer value){
        for (ConversationType item : ConversationType.values()){
            if (item.getValue().equals(value)){
                return item;
            }
        }
        throw new IllegalStateException("not support, value:{}" + value);
    }

}
