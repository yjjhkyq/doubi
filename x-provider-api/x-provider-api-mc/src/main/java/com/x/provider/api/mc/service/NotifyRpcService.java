package com.x.provider.api.mc.service;

import com.x.core.web.api.R;
import com.x.provider.api.mc.constants.McServiceNameConstants;
import com.x.provider.api.mc.model.ao.SendNotifyAO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "mcService", value = McServiceNameConstants.SERVICE_NAME, fallbackFactory = NotifyFallbackFactory.class)
public interface NotifyRpcService {

    @PostMapping(McServiceNameConstants.OSS_RPC_URL_PREFIX_NOTIFY + "/send")
    R<Void> sendNotify(@RequestBody SendNotifyAO sendNotifyAO);

}
