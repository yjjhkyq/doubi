package com.x.provider.statistic.service;

import com.x.provider.api.statistic.model.dto.ListMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueBatchRequestDTO;
import com.x.provider.api.statistic.model.event.IncMetricValueEvent;
import com.x.provider.statistic.model.domain.StatisticTotal;

import java.util.List;

public interface StatisticTotalService {
    void onStatTotal(IncMetricValueEvent statisticTotalEvent);
    void incStatTotal(StatisticTotal statisticTotal);
    void incStatTotals(List<StatisticTotal> statisticTotals);
    List<StatisticTotal> listStatisticTotal(ListMetricValueRequestDTO listStatTotalAO);
    List<StatisticTotal> listStatisticTotalBatch(ListMetricValueBatchRequestDTO listStatisticTotalBatchAO);
}
