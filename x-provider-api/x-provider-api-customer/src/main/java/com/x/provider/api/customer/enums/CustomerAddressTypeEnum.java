package com.x.provider.api.customer.enums;

import com.x.core.enums.IntegerEnum;

public enum CustomerAddressTypeEnum implements IntegerEnum {

    /**
     * 当前位置
     */
    CURRENT(1),

    /**
     * 家乡
     */
    HOME_TOWN(2),

    ;

    private final Integer value;

    CustomerAddressTypeEnum(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
