package com.x.provider.api.oss.service;

import com.x.core.web.api.R;
import com.x.provider.api.oss.constants.ServiceNameConstants;
import com.x.provider.api.oss.model.dto.oss.AttributeGreenRequestDTO;
import com.x.provider.api.oss.model.dto.oss.GreenRequestDTO;
import com.x.provider.api.oss.model.dto.oss.AttributeGreenResultDTO;
import com.x.provider.api.oss.service.factory.GreenFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "greenService", value = ServiceNameConstants.OSS_SERVICE, fallbackFactory = GreenFallbackFactory.class)
public interface GreenRpcService {
    @PostMapping(ServiceNameConstants.OSS_RPC_URL_PREFIX + "/attribute/green/async")
    R<Void> greenAttributeAsync(@RequestBody AttributeGreenRequestDTO attribute);

    @PostMapping(ServiceNameConstants.OSS_RPC_URL_PREFIX + "/attribute/green/sync")
    R<AttributeGreenResultDTO> greenAttributeSync(@RequestBody AttributeGreenRequestDTO attribute);

    @PostMapping(ServiceNameConstants.OSS_RPC_URL_PREFIX + "/green/sync")
    R<String> greenSync(@RequestBody GreenRequestDTO greenAO);
}
