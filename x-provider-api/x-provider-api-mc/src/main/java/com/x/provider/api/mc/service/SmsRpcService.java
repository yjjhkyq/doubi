package com.x.provider.api.mc.service;

import com.x.core.web.api.R;
import com.x.provider.api.mc.constants.McServiceNameConstants;
import com.x.provider.api.mc.model.dto.SendVerificationCodeDTO;
import com.x.provider.api.mc.model.dto.ValidateVerificationCodeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "mcService", value = McServiceNameConstants.SERVICE_NAME)
public interface SmsRpcService {
    @PostMapping(McServiceNameConstants.RPC_URL_PREFIX_SMS + "/verification/code/send")
    R<Void> sendVerificationCode(@RequestBody SendVerificationCodeDTO sendVerificationCodeAO);

    @PostMapping(McServiceNameConstants.RPC_URL_PREFIX_SMS + "/verification/code/validate")
    R<Void> validateVerificationCode(@RequestBody ValidateVerificationCodeDTO validateVerificationCodeAO);
}
