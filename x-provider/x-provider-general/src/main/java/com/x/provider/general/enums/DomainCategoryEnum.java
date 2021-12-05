package com.x.provider.general.enums;

import com.x.core.enums.IntegerEnum;

public enum DomainCategoryEnum implements IntegerEnum {
    ;

    private final Integer value;

    DomainCategoryEnum(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
