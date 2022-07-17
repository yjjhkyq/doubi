package com.x.provider.api.pay.enums;

import com.x.core.web.api.IErrorCode;

public enum PayErrorEnum implements IErrorCode {
    ASSET_NOT_ENOUGHT(92140002L, "资产不足"),
    ;
    private long code;
    private String message;

    PayErrorEnum(long code, String message) {
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
