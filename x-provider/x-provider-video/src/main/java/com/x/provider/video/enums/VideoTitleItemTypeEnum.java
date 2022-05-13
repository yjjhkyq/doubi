package com.x.provider.video.enums;

import com.x.core.enums.IntegerEnum;

public enum VideoTitleItemTypeEnum implements IntegerEnum {
    TEXT(1),
    TOPIC(2),
    AT(3),
    ;

    private final Integer value;

    VideoTitleItemTypeEnum(final Integer value){
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
