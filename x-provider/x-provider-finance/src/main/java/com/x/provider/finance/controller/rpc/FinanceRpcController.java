package com.x.provider.finance.controller.rpc;

import com.x.core.utils.BeanUtil;
import com.x.provider.api.finance.model.ao.ListIndustryAO;
import com.x.provider.api.finance.model.ao.ListSecurityAO;
import com.x.provider.api.finance.model.dto.IndustryDTO;
import com.x.provider.api.finance.model.dto.SecurityDTO;
import com.x.provider.api.finance.service.FinanceRpcService;
import com.x.provider.finance.service.FinanceDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/rpc/finance")
public class FinanceRpcController implements FinanceRpcService {

    private final FinanceDataService financeDataService;

    @Autowired
    public FinanceRpcController(FinanceDataService financeDataService){
        this.financeDataService =financeDataService;
    }

    @Override
    @PostMapping("/security/list")
    public List<SecurityDTO> listSecurity(@RequestBody ListSecurityAO listSecurityAO) {
        var securityList = financeDataService.listSecurity(listSecurityAO);
        return BeanUtil.prepare(securityList, SecurityDTO.class);
    }

    @Override
    @PostMapping("/industry/list")
    public List<IndustryDTO> listIndustry(@RequestBody ListIndustryAO listIndustryAO) {
        var industryList = financeDataService.listIndustry(listIndustryAO);
        return BeanUtil.prepare(industryList, IndustryDTO.class);
    }
}
