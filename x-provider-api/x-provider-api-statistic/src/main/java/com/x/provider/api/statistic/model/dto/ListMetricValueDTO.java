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
public class ListMetricValueDTO {
    private Long longValue;
    private Double doubleValue;
    private Date startDate;
    private int periodEnum;
    private int metricEnum;
    private String itemId;
    private int itemTypeEnum;
}
