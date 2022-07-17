package com.x.provider.api.pay.enums;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    OPENING(1, "开放状态"),
    FINISHED(2, "完成状态"),
    CLOSED(3, "关闭状态");

    private int code;
    private String desc;


    TransactionStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
