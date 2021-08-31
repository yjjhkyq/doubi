package com.x.provider.finance.controller.admin;

import com.x.core.web.api.R;
import com.x.provider.finance.service.FinanceDataService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/finance/data")
public class FinanceDataAdminController {

    private final FinanceDataService financeDataService;

    public FinanceDataAdminController(FinanceDataService financeDataService){
        this.financeDataService = financeDataService;
    }

    @PostMapping("/security/sync")
    public R<Void> syncSecurity(){
        financeDataService.syncSecurity();
        return R.ok();
    }

    @PostMapping("/industry/cn/spell/fill")
    public R<Void> fillIndustryCnSpell(){
        financeDataService.fillIndustryCnSpell();
        return R.ok();
    }
}
