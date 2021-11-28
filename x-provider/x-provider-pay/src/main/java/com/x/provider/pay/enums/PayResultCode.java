package com.x.provider.pay.enums;

import com.x.core.web.api.IErrorCode;

public enum PayResultCode implements IErrorCode {

    USER_WALLET_CREATED(20000, "用户钱包已创建"),
    USER_WALLET_NON_ACTIVE(20001, "钱包未激活"),
    USER_WALLET_LOCKED(20002, "钱包已锁定"),
    USER_WALLET_NOT_EXISTED(20003, "用户未开通钱包"),

    WALLET_BALANCE_INSUFFICIENT(20005, "余额不足"),

    USER_PAY_TOKEN_INVALID(20010, "用户钱包TOKEN验证失败"),
    USER_PAY_TOKEN_NOT_FOUND(20011, "用户钱包未验证"),

    WALLET_PASSWORD_ERROR(20020, "用户支付密码错误"),
    WALLET_PASSWORD_VALIDATED_LOCKED(20021, "密码错误次数达到限制, 请稍后再试"),

    RECEIVE_USER_ID_INVALID(20030, "密码错误次数达到限制, 请稍后再试"),

    BILL_IS_NOT_PREPARED(20040, "订单未在准备状态!"),
    BILL_IS_NOT_OPENING(20041, "订单未开放!"),

    AMOUNT_INVALID(20050, "金额非法!");


    private long code;
    private String message;

    private PayResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
