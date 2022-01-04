package com.x.provider.statistic.service;

import com.x.provider.api.statistic.model.ao.ListMetricValueAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueBatchAO;
import com.x.provider.api.statistic.model.dto.ListMetricValueMapDTO;
import com.x.provider.api.statistic.model.event.IncMetricValueEvent;
import com.x.provider.statistic.model.domain.StatisticTotal;

import java.util.List;

public interface StatisticTotalService {
    void onStatTotal(IncMetricValueEvent statisticTotalEvent);
    void incStatTotal(StatisticTotal statisticTotal);
    void incStatTotals(List<StatisticTotal> statisticTotals);
    ListMetricValueMapDTO listStatisticTotalMap(ListMetricValueBatchAO listStatisticTotalAO);
    List<StatisticTotal> listStatisticTotal(ListMetricValueAO listStatTotalAO);
    List<StatisticTotal> listStatisticTotalBatch(ListMetricValueBatchAO listStatisticTotalBatchAO);
}
