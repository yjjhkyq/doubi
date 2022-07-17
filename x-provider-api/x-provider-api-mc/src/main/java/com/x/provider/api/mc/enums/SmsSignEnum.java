package com.x.provider.api.mc.enums;

public enum SmsSignEnum {

    SIGN_CHINA("牛先生", "china");

    SmsSignEnum(String language, String sign){
        this.language = language;
        this.sign = sign;
    }

    private String sign;
    private String language;

    public String getSign() {
        return sign;
    }

    public String getLanguage() {
        return language;
    }
}
