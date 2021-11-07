package com.x.provider.api.statistic.model.event;

import lombok.Data;

@Data
public class StatisticTotalChangedEvent {
    private Long longValue;
    private Double doubleValue;
    private Integer statisticPeriodEnum;
    private Integer statTotalItemNameEnum;
    private String statisticObjectId;
    private Integer statisticObjectClassEnum;

    public StatisticTotalChangedEvent(){

    }

    public StatisticTotalChangedEvent(Integer statTotalItemNameEnum, Integer statisticPeriodEnum, Integer statisticObjectClassEnum, String statisticObjectId, Double doubleValue, Long longValue){
        this.statTotalItemNameEnum = statTotalItemNameEnum;
        this.statisticPeriodEnum = statisticPeriodEnum;
        this.statisticObjectClassEnum = statisticObjectClassEnum;
        this.statisticObjectId = statisticObjectId;
        this.doubleValue = doubleValue;
        this.longValue = longValue;
    }
}
