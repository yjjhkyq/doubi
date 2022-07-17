package com.x.provider.api.mc.enums;

import com.x.core.enums.IntegerEnum;

public enum GroupType implements IntegerEnum {
    /**
     * 私信用户用户
     */
    CUSTOMER(1),
    /**
     * 所有成员
     */
    ALL(2),
    ;

    private final Integer value;

    GroupType(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public static GroupType valueOf(Integer value){
        for (GroupType item : GroupType.values()){
            if (item.getValue().equals(value)){
                return item;
            }
        }
        throw new IllegalStateException("not support, value:{}" + value);
    }

}
