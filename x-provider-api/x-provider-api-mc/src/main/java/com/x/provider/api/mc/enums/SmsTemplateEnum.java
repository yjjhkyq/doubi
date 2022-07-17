package com.x.provider.api.mc.enums;

import com.x.core.enums.StringEnum;

public enum SmsTemplateEnum implements StringEnum {

    VERIFICATION_CODE("verfication_code");

    SmsTemplateEnum(String value){
        this.value = value;
    }

    private String value;

    @Override
    public String getValue() {
        return value;
    }
}
