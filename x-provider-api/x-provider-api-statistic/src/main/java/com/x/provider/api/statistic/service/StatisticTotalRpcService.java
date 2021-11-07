package com.x.provider.api.statistic.service;

import com.x.core.web.api.R;
import com.x.provider.api.statistic.constants.ServiceNameConstants;
import com.x.provider.api.statistic.model.ao.IncStatisticTotalValueAO;
import com.x.provider.api.statistic.model.ao.ListStatTotalAO;
import com.x.provider.api.statistic.model.ao.ListStatisticTotalBatchAO;
import com.x.provider.api.statistic.model.dto.ListStatisticTotalDTO;
import com.x.provider.api.statistic.model.dto.ListStatisticTotalMapDTO;
import com.x.provider.api.statistic.service.factory.StatisticTotalFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "statisticService", value = ServiceNameConstants.SERVICE, fallbackFactory = StatisticTotalFallbackFactory.class)
public interface StatisticTotalRpcService {
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/map")
    R<ListStatisticTotalMapDTO> listStatisticTotalMap(@RequestBody ListStatisticTotalBatchAO listStatisticTotalAO);
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/list/batch")
    R<List<ListStatisticTotalDTO>> listStatisticTotalBatch(@RequestBody ListStatisticTotalBatchAO listStatisticTotalBatchAO);
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/list")
    R<List<ListStatisticTotalDTO>> listStatisticTotal(@RequestBody ListStatTotalAO listStatTotalAO);
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STATISTIC_TOTAL + "/inc")
    R<Void> incStatisticTotal(@RequestBody IncStatisticTotalValueAO addStatisticTotalValueAO);
}
