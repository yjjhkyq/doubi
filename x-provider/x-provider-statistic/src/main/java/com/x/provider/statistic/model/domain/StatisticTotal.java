package com.x.provider.statistic.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticTotal {
    private Long longValue;
    private Double doubleValue;
    private Date startDate;
    private int statisticPeriodEnum;
    private int statTotalItemNameEnum;
    private String statisticObjectId;
    private int statisticObjectClassEnum;
}
