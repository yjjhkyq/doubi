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
public class ListStatTotalAO {
    private int statisticPeriod;
    private int statTotalItemNameEnum;
    private List<String> statisticObjectIds;
    private int statisticObjectClassEnum;
    private Date date;
}
