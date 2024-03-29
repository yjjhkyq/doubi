package com.x.provider.api.mc.enums;

import com.x.core.web.api.IErrorCode;

public enum McErrorEnum implements IErrorCode {
    VERIFICATION_CODE_ERROR(92070002L, "验证码错误，请重新输入"),
    PHPONE_NUMBER_DAILY_LIMIT(92070003L, "今日发送了太多短信，请明天在再发送短信"),
    PHPONE_NUMBER_HOUR_LIMIT(92070004L, "发送太多短信，请1小时候后在发送短信"),
    PHONE_NOMBER_THIRTY_SECOND_LIMIT(92070005L, "请勿频繁发送短信，稍后在试"),
    ;
    private long code;
    private String message;

    McErrorEnum(long code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
