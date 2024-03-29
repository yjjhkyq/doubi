package com.x.provider.api.general.service;

import com.x.core.web.api.R;
import com.x.provider.api.general.constants.ServiceNameConstants;
import com.x.provider.api.general.model.dto.IsStarredRequestDTO;
import com.x.provider.api.general.model.dto.ListStarRequestDTO;
import com.x.provider.api.general.model.dto.StarRequestDTO;
import com.x.provider.api.general.model.dto.StarDTO;
import com.x.provider.api.general.service.factory.StarFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "generalStarService", value = ServiceNameConstants.SERVICE, fallbackFactory = StarFallbackFactory.class)
public interface StarRpcService {
    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STAR + "/is/starred")
    R<Boolean> isStarred(@RequestBody IsStarredRequestDTO isStarred);

    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STAR + "/create")
    R<Boolean> star(@RequestBody StarRequestDTO starAO);

    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX_STAR + "/list")
    R<List<StarDTO>> listStar(@RequestBody ListStarRequestDTO listStarAO);
}
