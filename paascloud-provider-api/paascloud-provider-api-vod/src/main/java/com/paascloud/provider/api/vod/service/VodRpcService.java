package com.paascloud.provider.api.vod.service;

import com.paascloud.core.web.api.R;
import com.paascloud.provider.api.vod.constants.ServiceNameConstants;
import com.paascloud.provider.api.vod.model.ao.GetContentReviewResultAO;
import com.paascloud.provider.api.vod.service.factory.VodFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "vodService", value = ServiceNameConstants.VOD_SERVICE, fallbackFactory = VodFallbackFactory.class)
public interface VodRpcService {

    @PostMapping(ServiceNameConstants.VOD_RPC_URL_PREFIX + "/contentReview")
    R<Void> contentReview(@RequestBody GetContentReviewResultAO getContentReviewResultAO);
}
