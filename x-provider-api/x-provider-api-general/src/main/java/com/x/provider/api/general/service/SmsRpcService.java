package com.x.provider.api.general.service;

import com.x.core.web.api.IErrorCode;
import com.x.core.web.api.R;
import com.x.provider.api.general.constants.ServiceNameConstants;
import com.x.provider.api.general.model.ao.*;
import com.x.provider.api.general.service.factory.StarFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "generalStarService", value = ServiceNameConstants.SERVICE)
public interface SmsRpcService {
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_SMS + "/verification/code/send")
    R<Void> sendVerificationCode(@RequestBody SendVerificationCodeAO sendVerificationCodeAO);

    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_SMS + "/verification/code/validate")
    R<Void> validateVerificationCode(@RequestBody ValidateVerificationCodeAO validateVerificationCodeAO);
}
