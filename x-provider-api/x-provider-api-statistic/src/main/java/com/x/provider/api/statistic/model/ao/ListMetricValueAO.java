package com.x.provider.api.statistic.model.ao;

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
public class ListMetricValueAO {
    private int periodEnum;
    private int metricEnum;
    private List<String> itemIds;
    private int itemTypeEnum;
    private Date date;
}
