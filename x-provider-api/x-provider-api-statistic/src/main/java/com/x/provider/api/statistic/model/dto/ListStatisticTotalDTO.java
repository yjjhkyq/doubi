package com.x.provider.api.statistic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListStatisticTotalDTO {
    private Long longValue;
    private Double doubleValue;
    private Date startDate;
    private int statisticPeriodEnum;
    private int statTotalItemNameEnum;
    private String statisticObjectId;
    private int statisticObjectClassEnum;
}
