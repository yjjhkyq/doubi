package com.x.core.domain;

import com.x.core.enums.IntegerEnum;

public enum SuggestionTypeEnum implements IntegerEnum {

    PASS(1),
    BLOCK(2),
    REVIEW(3),
    ;

    private Integer value;

    SuggestionTypeEnum(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
