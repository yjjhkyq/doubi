package com.x.provider.api.finance.service;

import com.x.provider.api.finance.constants.ServiceNameConstants;
import com.x.provider.api.finance.model.ao.ListIndustryAO;
import com.x.provider.api.finance.model.ao.ListSecurityAO;
import com.x.provider.api.finance.model.dto.IndustryDTO;
import com.x.provider.api.finance.model.dto.SecurityDTO;
import com.x.provider.api.finance.service.factory.FinianceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "financeService", value = ServiceNameConstants.FINANCE_SERVICE, fallbackFactory = FinianceFallbackFactory.class)
public interface FinanceRpcService {

    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX + "/security/list")
    List<SecurityDTO> listSecurity(@RequestBody ListSecurityAO listSecurityAO);

    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX + "/industry/list")
    List<IndustryDTO> listIndustry(@RequestBody ListIndustryAO listIndustryAO);
}
