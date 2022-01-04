package com.x.provider.api.general.service;

import com.x.core.web.api.R;
import com.x.core.web.page.PageList;
import com.x.provider.api.general.constants.ServiceNameConstants;
import com.x.provider.api.general.model.ao.IsStarredAO;
import com.x.provider.api.general.model.ao.ListStarAO;
import com.x.provider.api.general.model.ao.StarAO;
import com.x.provider.api.general.model.dto.StarDTO;
import com.x.provider.api.general.service.factory.StarFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "generalStarService", value = ServiceNameConstants.SERVICE, fallbackFactory = StarFallbackFactory.class)
public interface StarRpcService {
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STAR + "/is/starred")
    R<Boolean> isStarred(@RequestBody IsStarredAO isStarred);

    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STAR + "/create")
    R<Boolean> star(@RequestBody StarAO starAO);
}
