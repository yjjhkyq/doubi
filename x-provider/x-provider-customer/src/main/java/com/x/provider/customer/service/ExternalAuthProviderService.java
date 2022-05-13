package com.x.provider.customer.service;

import com.x.provider.customer.model.ao.ExternalAuthenticationAO;
import com.x.provider.customer.model.domain.ExternalAuthenticationRecord;

public interface ExternalAuthProviderService {
    ExternalAuthenticationRecord authenticate(ExternalAuthenticationAO externalAuthenticationAO);
    boolean support(ExternalAuthenticationAO externalAuthenticationAO);
}
