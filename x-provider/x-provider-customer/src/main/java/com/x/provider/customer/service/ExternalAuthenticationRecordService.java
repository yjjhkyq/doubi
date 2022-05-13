package com.x.provider.customer.service;

import com.x.provider.customer.model.domain.ExternalAuthenticationRecord;

public interface ExternalAuthenticationRecordService {
    ExternalAuthenticationRecord get(Long id, Integer provider, String externalIdentifier, String unionExternalIdentifier);
    void insert(ExternalAuthenticationRecord externalAuthenticationRecord);
}
