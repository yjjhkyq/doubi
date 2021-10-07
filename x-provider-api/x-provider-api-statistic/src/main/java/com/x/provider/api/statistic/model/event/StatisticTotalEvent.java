package com.x.provider.api.statistic.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticTotalEvent{
    private Long longValue;
    private Double doubleValue;
    private int statisticPeriodEnum;
    private int statTotalItemNameEnum;
    private String statisticObjectId;
    private int statisticObjectClassEnum;
}
