package com.paascloud.provider.api.oss.service;

import com.paascloud.core.web.api.R;
import com.paascloud.provider.api.oss.constants.ServiceNameConstants;
import com.paascloud.provider.api.oss.model.ao.AttributeGreenRpcAO;
import com.paascloud.provider.api.oss.model.dto.AttributeGreenResultDTO;
import com.paascloud.provider.api.oss.service.factory.GreenFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "greenService", value = ServiceNameConstants.OSS_SERVICE, fallbackFactory = GreenFallbackFactory.class)
public interface GreenRpcService {
    @PostMapping(ServiceNameConstants.OSS_RPC_URL_PREFIX + "/greenAttributeAsync")
    R<Void> greenAttributeAsync(@RequestBody AttributeGreenRpcAO attribute);

    @PostMapping(ServiceNameConstants.OSS_RPC_URL_PREFIX + "/greenAttributeSync")
    R<AttributeGreenResultDTO> greenAttributeSync(@RequestBody AttributeGreenRpcAO attribute);
}
