package com.x.provider.api.statistic.enums;

public enum StatisticPeriodEnum {
    DAY(1),
    WEEK(2),
    MONTH(3),
    YEAR(4),
    ALL(5),
    ;

    private final int value;

    StatisticPeriodEnum(int value){
        this.value = value;
    }

    public static StatisticPeriodEnum valueOf(int value){
        for (StatisticPeriodEnum statisticPeriodEnum : StatisticPeriodEnum.values()) {
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
