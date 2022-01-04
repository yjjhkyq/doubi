package com.x.provider.api.statistic.enums;

public enum MetricEnum {
    STAR_COUNT(1),
    PLAY_COUNT(2),
    COUNT(3),
    REPLY_COUNT(5),
    SCORE(50),
    ;

    private final int value;

    MetricEnum(int value){
        this.value = value;
    }

    public static MetricEnum valueOf(int value){
        for (MetricEnum item : MetricEnum.values()) {
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
