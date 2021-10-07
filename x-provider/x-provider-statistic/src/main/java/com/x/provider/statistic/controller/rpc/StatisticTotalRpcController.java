package com.x.provider.statistic.controller.rpc;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseRpcController;
import com.x.provider.api.statistic.model.ao.IncStatisticTotalValueAO;
import com.x.provider.api.statistic.model.ao.ListStatTotalAO;
import com.x.provider.api.statistic.model.ao.ListStatisticTotalBatchAO;
import com.x.provider.api.statistic.model.dto.ListStatisticTotalDTO;
import com.x.provider.api.statistic.model.dto.ListStatisticTotalMapDTO;
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
    public R<ListStatisticTotalMapDTO> listStatisticTotalMap(ListStatisticTotalBatchAO listStatisticTotalAO) {
        return null;
    }

    @Override
    @PostMapping("list")
    public R<List<ListStatisticTotalDTO>> listStatisticTotal(@RequestBody ListStatTotalAO listStatTotalAO) {
        List<StatisticTotal> statisticTotals = statisticTotalService.listStatisticTotal(listStatTotalAO);
        return R.ok(BeanUtil.prepare(statisticTotals, ListStatisticTotalDTO.class));
    }

    @Override
    @PostMapping("inc")
    public R<Void> incStatisticTotal(@RequestBody IncStatisticTotalValueAO addStatisticTotalValueAO) {
        statisticTotalService.incStatTotal(BeanUtil.prepare(addStatisticTotalValueAO, StatisticTotal.class));
        return R.ok();
    }
}
