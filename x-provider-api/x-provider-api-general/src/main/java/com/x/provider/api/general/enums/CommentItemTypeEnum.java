package com.x.provider.api.general.enums;

import com.x.core.enums.IntegerEnum;

public enum CommentItemTypeEnum implements IntegerEnum {
    VIDEO(1),
    COMMENT(2)
    ;

    private Integer value;

    CommentItemTypeEnum(Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
