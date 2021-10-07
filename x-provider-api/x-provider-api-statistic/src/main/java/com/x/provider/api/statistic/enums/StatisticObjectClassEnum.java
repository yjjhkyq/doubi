package com.x.provider.api.statistic.enums;

public enum StatisticObjectClassEnum {
    VIDEO(1),
    COMMENT(2),
    USER(3),
    ;

    private final int value;

    StatisticObjectClassEnum(int value){
        this.value = value;
    }

    public static StatisticObjectClassEnum valueOf(int value){
        for (StatisticObjectClassEnum item : StatisticObjectClassEnum.values()) {
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
