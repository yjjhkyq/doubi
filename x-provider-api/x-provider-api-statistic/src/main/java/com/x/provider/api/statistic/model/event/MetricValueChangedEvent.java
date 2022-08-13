package com.x.provider.api.statistic.model.event;

import lombok.Data;

@Data
public class MetricValueChangedEvent {
    private Long longValue;
    private Double doubleValue;
    private Integer period;
    private Integer metric;
    private String itemId;
    private Integer itemType;

    public MetricValueChangedEvent(){

    }

    public MetricValueChangedEvent(Integer metric, Integer period, Integer itemType, String itemId, Double doubleValue, Long longValue){
        this.metric = metric;
        this.period = period;
        this.itemType = itemType;
        this.itemId = itemId;
        this.doubleValue = doubleValue;
        this.longValue = longValue;
    }
}
