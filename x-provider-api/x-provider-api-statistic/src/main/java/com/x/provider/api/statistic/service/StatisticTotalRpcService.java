package com.x.provider.api.statistic.service;

import com.x.core.web.api.R;
import com.x.provider.api.statistic.constants.ServiceNameConstants;
import com.x.provider.api.statistic.model.ao.IncMetricValueValueAO;
import com.x.provider.api.statistic.model.ao.IncMetricValuesAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueBatchAO;
import com.x.provider.api.statistic.model.dto.ListMetricValueDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueMapDTO;
import com.x.provider.api.statistic.service.factory.StatisticTotalFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "statisticService", value = ServiceNameConstants.SERVICE, fallbackFactory = StatisticTotalFallbackFactory.class)
public interface StatisticTotalRpcService {
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/map")
    R<ListMetricValueMapDTO> listStatisticTotalMap(@RequestBody ListMetricValueBatchAO listStatisticTotalAO);
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/list/batch")
    R<List<ListMetricValueDTO>> listStatisticTotalBatch(@RequestBody ListMetricValueBatchAO listStatisticTotalBatchAO);
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/list")
    R<List<ListMetricValueDTO>> listStatisticTotal(@RequestBody ListMetricValueAO listStatTotalAO);
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/inc")
    R<Void> incStatisticTotal(@RequestBody IncMetricValueValueAO addStatisticTotalValueAO);
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/incs")
    R<Void> incStatisticTotals(@RequestBody IncMetricValuesAO incStatisticTotalValuesAO);
}
