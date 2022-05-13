package com.x.provider.customer.service.impl.authext;

import cn.hutool.core.util.StrUtil;
import com.x.core.utils.SpringUtils;
import com.x.provider.customer.model.ao.ExternalAuthenticationAO;
import com.x.provider.customer.model.domain.Customer;
import com.x.provider.customer.model.domain.ExternalAuthenticationRecord;
import com.x.provider.customer.service.AuthenticationService;
import com.x.provider.customer.service.CustomerService;
import com.x.provider.customer.service.ExternalAuthProviderService;
import com.x.provider.customer.service.ExternalAuthenticationRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExternalAuthEngine {

    private static final List<ExternalAuthProviderService> externalAuthenticationServiceList = SpringUtils.getBanListOfType(ExternalAuthProviderService.class);

    private final AuthenticationService authenticationService;
    private final CustomerService customerService;
    private final ExternalAuthenticationRecordService externalAuthenticationRecordService;

    public ExternalAuthEngine(AuthenticationService authenticationService,
                              CustomerService customerService,
                              ExternalAuthenticationRecordService externalAuthenticationRecordService){
        this.authenticationService = authenticationService;
        this.customerService = customerService;
        this.externalAuthenticationRecordService = externalAuthenticationRecordService;
    }

    @Transactional(rollbackFor = Exception.class)
    public String authenticate(ExternalAuthenticationAO externalAuthenticationAO){
        for (ExternalAuthProviderService item: externalAuthenticationServiceList) {
            if (item.support(externalAuthenticationAO)){
                ExternalAuthenticationRecord authRecord = item.authenticate(externalAuthenticationAO);
                Customer customer = authRecord.getCustomerId() == null ? customerService.registerCustomer(new Customer()) : customerService.getCustomer(authRecord.getCustomerId());
                if (authRecord.getCustomerId() == null){
                    authRecord.setCustomerId(customer.getId());
                    authRecord.setProvider(externalAuthenticationAO.getProvider());
                    externalAuthenticationRecordService.insert(authRecord);
                }
                return authenticationService.signIn(customer);
            }
        }
        throw new IllegalStateException(StrUtil.format("provider impl not find, provider:{}", externalAuthenticationAO.getProvider()));
    }
}
