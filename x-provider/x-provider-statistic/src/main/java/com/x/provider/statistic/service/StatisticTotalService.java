package com.x.provider.statistic.service;

import com.x.provider.api.statistic.model.ao.ListStatTotalAO;
import com.x.provider.api.statistic.model.ao.ListStatisticTotalBatchAO;
import com.x.provider.api.statistic.model.dto.ListStatisticTotalMapDTO;
import com.x.provider.api.statistic.model.event.StatisticTotalEvent;
import com.x.provider.statistic.model.domain.StatisticTotal;

import java.util.List;

public interface StatisticTotalService {
    void onStatTotal(StatisticTotalEvent statisticTotalEvent);
    void incStatTotal(StatisticTotal statisticTotal);
    ListStatisticTotalMapDTO listStatisticTotalMap(ListStatisticTotalBatchAO listStatisticTotalAO);
    List<StatisticTotal> listStatisticTotal(ListStatTotalAO listStatTotalAO);
    List<StatisticTotal> listStatisticTotalBatch(ListStatisticTotalBatchAO listStatisticTotalBatchAO);
}
