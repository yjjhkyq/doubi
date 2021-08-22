package com.x.provider.api.vod.service;

import com.x.core.web.api.R;
import com.x.provider.api.vod.constants.ServiceNameConstants;
import com.x.provider.api.vod.model.ao.GetContentReviewResultAO;
import com.x.provider.api.vod.model.ao.ListMediaUrlAO;
import com.x.provider.api.vod.service.factory.VodFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(contextId = "vodService", value = ServiceNameConstants.VOD_SERVICE, fallbackFactory = VodFallbackFactory.class)
public interface VodRpcService {

    @PostMapping(ServiceNameConstants.VOD_RPC_URL_PREFIX + "/contentReview")
    R<Void> contentReview(@RequestBody GetContentReviewResultAO getContentReviewResultAO);

    @PostMapping(ServiceNameConstants.VOD_RPC_URL_PREFIX + "/listMediaUrl")
    Map<String, String> listMediaUrl(@RequestBody ListMediaUrlAO listMediaUrlAO);
}
