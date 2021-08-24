package com.x.provider.api.oss.service;

import com.x.core.web.api.R;
import com.x.provider.api.oss.constants.ServiceNameConstants;
import com.x.provider.api.oss.model.ao.AttributeGreenRpcAO;
import com.x.provider.api.oss.model.dto.AttributeGreenResultDTO;
import com.x.provider.api.oss.service.factory.GreenFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "greenService", value = ServiceNameConstants.OSS_SERVICE, fallbackFactory = GreenFallbackFactory.class)
public interface GreenRpcService {
    @PostMapping(ServiceNameConstants.OSS_RPC_URL_PREFIX + "/attribute/green/async")
    R<Void> greenAttributeAsync(@RequestBody AttributeGreenRpcAO attribute);

    @PostMapping(ServiceNameConstants.OSS_RPC_URL_PREFIX + "/attribute/green/sync")
    R<AttributeGreenResultDTO> greenAttributeSync(@RequestBody AttributeGreenRpcAO attribute);
}
