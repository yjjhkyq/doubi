package com.x.provider.api.mc.service;

import com.x.core.web.api.R;
import com.x.provider.api.mc.constants.McServiceNameConstants;
import com.x.provider.api.mc.model.dto.SendMessageRequestDTO;
import com.x.provider.api.mc.model.dto.SendMessageRawDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "mcService", value = McServiceNameConstants.SERVICE_NAME)
public interface MessageRpcService {

    @PostMapping(McServiceNameConstants.MC_RPC_URL_PREFIX_MESSAGE + "/send")
    R<Long> sendMessage(@RequestBody SendMessageRequestDTO sendMessageAO);

    @PostMapping(McServiceNameConstants.MC_RPC_URL_PREFIX_MESSAGE + "/send/raw")
    R<Long> sendMessageRaw(@RequestBody SendMessageRawDTO sendMessageAO);
}
