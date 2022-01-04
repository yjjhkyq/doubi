package com.x.provider.statistic.controller.admin;

import com.x.core.web.api.R;
import com.x.provider.api.statistic.model.event.IncMetricValueEvent;
import com.x.provider.statistic.service.StatisticTotalService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/statistic")
public class StatisticAdminController {

    private final StatisticTotalService totalService;

    public StatisticAdminController(StatisticTotalService totalService){
        this.totalService = totalService;
    }

    @PostMapping("/on/total/event")
    public R<Void> onStatTotal(@RequestBody IncMetricValueEvent event){
        totalService.onStatTotal(event);
        return R.ok();
    }
}
