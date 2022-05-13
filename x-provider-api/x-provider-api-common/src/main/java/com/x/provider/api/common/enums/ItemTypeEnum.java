package com.x.provider.api.common.enums;

public enum ItemTypeEnum {
    VIDEO(1),
    COMMENT(2),
    CUSTOMER(3),
    ;

    private final int value;

    ItemTypeEnum(int value){
        this.value = value;
    }

    public static ItemTypeEnum valueOf(int value){
        for (ItemTypeEnum item : ItemTypeEnum.values()) {
            if (item.value == value){
                return item;
            }
        }
        throw new IllegalStateException("value:" + value);
    }

    public int getValue(){
        return value;
    }
}
