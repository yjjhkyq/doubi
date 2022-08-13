package com.x.provider.api.statistic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncMetricValueRequestDTO {
    private Long longValue;
    private Double doubleValue;
    private int period;
    private int metric;
    private String itemId;
    private int itemType;
}
