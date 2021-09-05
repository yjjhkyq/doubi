package com.x.provider.finance.component;

import com.x.provider.finance.service.FinanceDataService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JobComponent {

    private final FinanceDataService financeDataService;

    public JobComponent(FinanceDataService financeDataService){
        this.financeDataService = financeDataService;
    }

    @XxlJob("syncFinanceData")
    public void syncFinanceDataJobHandler() throws Exception {
        financeDataService.syncSecurity();;
    }
}
