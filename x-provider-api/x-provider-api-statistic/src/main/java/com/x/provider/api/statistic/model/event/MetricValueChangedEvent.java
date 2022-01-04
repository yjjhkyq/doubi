package com.x.provider.api.statistic.model.event;

import lombok.Data;

@Data
public class MetricValueChangedEvent {
    private Long longValue;
    private Double doubleValue;
    private Integer periodEnum;
    private Integer metricEnum;
    private String itemId;
    private Integer itemTypeEnum;

    public MetricValueChangedEvent(){

    }

    public MetricValueChangedEvent(Integer metricEnum, Integer periodEnum, Integer itemTypeEnum, String itemId, Double doubleValue, Long longValue){
        this.metricEnum = metricEnum;
        this.periodEnum = periodEnum;
        this.itemTypeEnum = itemTypeEnum;
        this.itemId = itemId;
        this.doubleValue = doubleValue;
        this.longValue = longValue;
    }
}
