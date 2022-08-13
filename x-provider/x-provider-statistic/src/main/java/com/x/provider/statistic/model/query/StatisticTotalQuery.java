package com.x.provider.statistic.model.query;

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
public class StatisticTotalQuery {
    private Long id;
    private Date startDate;
    private Integer period;
    private Integer metric;
    private String itemId;
    private Integer itemType;
    private List<String> itemIdList;

}
