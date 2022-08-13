package com.x.provider.statistic.controller.rpc;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseRpcController;
import com.x.provider.api.statistic.model.dto.IncMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.IncMetricValuesRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueBatchRequestDTO;
import com.x.provider.api.statistic.model.dto.MetricValueDTO;
import com.x.provider.api.statistic.service.StatisticTotalRpcService;
import com.x.provider.statistic.model.domain.StatisticTotal;
import com.x.provider.statistic.service.StatisticTotalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rpc/statistic/total")
public class StatisticTotalRpcController extends BaseRpcController implements StatisticTotalRpcService {

    private final StatisticTotalService statisticTotalService;

    public StatisticTotalRpcController(StatisticTotalService statisticTotalService){
        this.statisticTotalService = statisticTotalService;
    }

    @PostMapping("list/batch")
    @Override
    public R<List<MetricValueDTO>> listStatisticTotalBatch(@RequestBody ListMetricValueBatchRequestDTO listStatisticTotalBatchAO) {
        List<StatisticTotal> statisticTotals = statisticTotalService.listStatisticTotalBatch(listStatisticTotalBatchAO);
        return R.ok(BeanUtil.prepare(statisticTotals, MetricValueDTO.class));
    }

    @Override
    @PostMapping("list")
    public R<List<MetricValueDTO>> listStatisticTotal(@RequestBody ListMetricValueRequestDTO listStatTotalAO) {
        List<StatisticTotal> statisticTotals = statisticTotalService.listStatisticTotal(listStatTotalAO);
        return R.ok(BeanUtil.prepare(statisticTotals, MetricValueDTO.class));
    }

    @Override
    @PostMapping("inc")
    public R<Void> incStatisticTotal(@RequestBody IncMetricValueRequestDTO addStatisticTotalValueAO) {
        statisticTotalService.incStatTotal(BeanUtil.prepare(addStatisticTotalValueAO, StatisticTotal.class));
        return R.ok();
    }

    @Override
    @PostMapping("incs")
    public R<Void> incStatisticTotals(IncMetricValuesRequestDTO incStatisticTotalValuesAO) {
        statisticTotalService.incStatTotals(BeanUtil.prepare(incStatisticTotalValuesAO.getIncMetricValues(), StatisticTotal.class));
        return R.ok();
    }
}
