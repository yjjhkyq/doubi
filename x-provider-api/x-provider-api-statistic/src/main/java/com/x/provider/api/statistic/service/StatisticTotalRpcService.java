package com.x.provider.api.statistic.service;

import com.x.core.web.api.R;
import com.x.provider.api.statistic.constants.ServiceNameConstants;
import com.x.provider.api.statistic.model.dto.IncMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.IncMetricValuesRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueBatchRequestDTO;
import com.x.provider.api.statistic.model.dto.MetricValueDTO;
import com.x.provider.api.statistic.service.factory.StatisticTotalFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "statisticService", value = ServiceNameConstants.SERVICE, fallbackFactory = StatisticTotalFallbackFactory.class)
public interface StatisticTotalRpcService {
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/list/batch")
    R<List<MetricValueDTO>> listStatisticTotalBatch(@RequestBody ListMetricValueBatchRequestDTO listStatisticTotalBatchAO);
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/list")
    R<List<MetricValueDTO>> listStatisticTotal(@RequestBody ListMetricValueRequestDTO listStatTotalAO);
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/inc")
    R<Void> incStatisticTotal(@RequestBody IncMetricValueRequestDTO addStatisticTotalValueAO);
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/incs")
    R<Void> incStatisticTotals(@RequestBody IncMetricValuesRequestDTO incStatisticTotalValuesAO);
}
