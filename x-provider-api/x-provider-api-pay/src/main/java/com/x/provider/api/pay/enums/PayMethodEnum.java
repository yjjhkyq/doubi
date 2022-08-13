package com.x.provider.api.pay.enums;

import com.x.core.enums.IntegerEnum;

public enum PayMethodEnum implements IntegerEnum {
    Wx(1)
    ;

    private Integer value;

    PayMethodEnum(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public static PayMethodEnum valueOf(Integer value){
        for (PayMethodEnum payMethodEnum : PayMethodEnum.values()) {
            if (payMethodEnum.getValue().equals(value)){
                return payMethodEnum;
            }
        }
        throw new IllegalStateException(String.valueOf(value));
    }
}
