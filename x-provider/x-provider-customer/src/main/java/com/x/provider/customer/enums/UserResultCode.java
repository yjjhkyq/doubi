package com.x.provider.customer.enums;

import com.x.core.web.api.IErrorCode;

/**
 * start from 10000 ~ 2000
 * Created by macro on 2019/4/19.
 */
public enum UserResultCode implements IErrorCode {
    USER_NAME_EXISTED(10000, "用户名已经存在"),
    USER_NAME_OR_PWD_ERROR(10001, "用户名或者密码错误"),
    CUSTOMER_NOT_ACTIVE(10002, "账号未激活"),
    PASSWORD_ERROR(10003, "密码错误"),
    USER_PHONE_EXISTED(10004, "手机号已注册"),
    USER_PHONE_ERROR(10005, "手机号输入错误"),
    USER_PHONE_BOUND(10006, "手机号已绑定"),
    WEIXIN_MICRO_APP_AUTH_FAILED(10007, "微信小程序授权失败，请稍后重试"),
    LAST(-1, "");

    private long code;
    private String message;

    private UserResultCode(long code, String message) {
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
