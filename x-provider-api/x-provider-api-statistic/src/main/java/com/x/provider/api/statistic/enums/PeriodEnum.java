package com.x.provider.api.statistic.enums;

public enum PeriodEnum {
    DAY(1),
    WEEK(2),
    MONTH(3),
    YEAR(4),
    ALL(5),
    ;

    private final int value;

    PeriodEnum(int value){
        this.value = value;
    }

    public static PeriodEnum valueOf(int value){
        for (PeriodEnum statisticPeriodEnum : PeriodEnum.values()) {
            if (statisticPeriodEnum.value == value){
                return statisticPeriodEnum;
            }
        }
        throw new IllegalStateException("value:" + value);
    }

    public int getValue(){
        return value;
    }
}
