package com.x.provider.statistic.controller.rpc;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseRpcController;
import com.x.provider.api.statistic.model.ao.IncMetricValueValueAO;
import com.x.provider.api.statistic.model.ao.IncMetricValuesAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueBatchAO;
import com.x.provider.api.statistic.model.dto.ListMetricValueDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueMapDTO;
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

    @Override
    @PostMapping("map")
    public R<ListMetricValueMapDTO> listStatisticTotalMap(@RequestBody ListMetricValueBatchAO listStatisticTotalAO) {
        List<StatisticTotal> statisticTotals = statisticTotalService.listStatisticTotalBatch(listStatisticTotalAO);
        return null;
    }

    @PostMapping("list/batch")
    @Override
    public R<List<ListMetricValueDTO>> listStatisticTotalBatch(@RequestBody ListMetricValueBatchAO listStatisticTotalBatchAO) {
        List<StatisticTotal> statisticTotals = statisticTotalService.listStatisticTotalBatch(listStatisticTotalBatchAO);
        return R.ok(BeanUtil.prepare(statisticTotals, ListMetricValueDTO.class));
    }

    @Override
    @PostMapping("list")
    public R<List<ListMetricValueDTO>> listStatisticTotal(@RequestBody ListMetricValueAO listStatTotalAO) {
        List<StatisticTotal> statisticTotals = statisticTotalService.listStatisticTotal(listStatTotalAO);
        return R.ok(BeanUtil.prepare(statisticTotals, ListMetricValueDTO.class));
    }

    @Override
    @PostMapping("inc")
    public R<Void> incStatisticTotal(@RequestBody IncMetricValueValueAO addStatisticTotalValueAO) {
        statisticTotalService.incStatTotal(BeanUtil.prepare(addStatisticTotalValueAO, StatisticTotal.class));
        return R.ok();
    }

    @Override
    @PostMapping("incs")
    public R<Void> incStatisticTotals(IncMetricValuesAO incStatisticTotalValuesAO) {
        statisticTotalService.incStatTotals(BeanUtil.prepare(incStatisticTotalValuesAO.getIncMetricValues(), StatisticTotal.class));
        return R.ok();
    }
}
