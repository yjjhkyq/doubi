package com.x.provider.pay.enums.bill;

import com.x.provider.pay.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum BillStatus implements BaseEnum {
    PREPARED(0, "准备状态"),
    OPENING(1, "开放状态"),
    FINISHED(2, "完成状态"),
    CLOSED(3, "关闭状态");

    private int code;
    private String desc;


    BillStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BillStatus getEnum(int billStatusCode) {
        return (BillStatus) BaseEnum.getEnum(BillStatus.class, billStatusCode);
    }

}
