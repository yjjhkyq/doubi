package com.x.provider.api.mc.enums;

import com.x.core.enums.LongEnum;

public enum  SystemGroupIdEnum implements LongEnum {

    ALL(1L),
    ;

    private Long id;

    SystemGroupIdEnum(Long id){
        this.id = id;
    }

    @Override
    public Long getValue() {
        return id;
    }
}
