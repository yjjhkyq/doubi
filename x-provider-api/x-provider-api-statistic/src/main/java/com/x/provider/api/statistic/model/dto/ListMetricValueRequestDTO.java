package com.x.provider.api.statistic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListMetricValueRequestDTO {
    private int period;
    private int metric;
    private List<String> itemIds;
    private int itemType;
    private Date date;
}
