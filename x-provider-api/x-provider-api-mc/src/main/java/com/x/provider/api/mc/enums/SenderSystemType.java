package com.x.provider.api.mc.enums;

import com.x.core.enums.LongEnum;

public enum SenderSystemType implements LongEnum {
    FAN(1L),
    INACTIVE(2L),
    SYS_NOTIFY(3L),
    SECRETARY(4L),
    ;

    private final Long value;

    private SenderSystemType(Long value){
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }
}
