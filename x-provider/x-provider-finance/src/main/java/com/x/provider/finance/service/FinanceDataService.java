package com.x.provider.finance.service;

import com.x.provider.api.finance.model.ao.ListIndustryAO;
import com.x.provider.api.finance.model.ao.ListSecurityAO;
import com.x.provider.finance.model.domain.Industry;
import com.x.provider.finance.model.domain.Security;

import java.util.Date;
import java.util.List;

public interface FinanceDataService {
    void syncSecurity();
    void fillIndustryCnSpell();
    List<Industry> listIndustry(List<Long> ids, Date updateOnUtcAfter);
    List<Security> listSecurity(List<Long> ids, Date updateOnUtcAfter);
    List<Security> listSecurity(ListSecurityAO listSecurityAO);
    List<Industry> listIndustry(ListIndustryAO listIndustryAO);
}
