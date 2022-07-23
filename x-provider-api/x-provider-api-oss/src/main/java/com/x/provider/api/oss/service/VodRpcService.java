package com.x.provider.api.oss.service;

import com.x.core.web.api.R;
import com.x.provider.api.oss.constants.ServiceNameConstants;
import com.x.provider.api.oss.model.ao.vod.DeleteMediaAO;
import com.x.provider.api.oss.model.ao.vod.GetContentReviewResultAO;
import com.x.provider.api.oss.model.ao.vod.ListMediaAO;
import com.x.provider.api.oss.model.ao.vod.ListMediaUrlAO;
import com.x.provider.api.oss.model.dto.vod.MediaInfoDTO;
import com.x.provider.api.oss.service.factory.VodFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "vodService", value = ServiceNameConstants.OSS_SERVICE, fallbackFactory = VodFallbackFactory.class)
public interface VodRpcService {

    @PostMapping(ServiceNameConstants.VOD_RPC_URL_PREFIX + "/content/review")
    R<Void> contentReview(@RequestBody GetContentReviewResultAO getContentReviewResultAO);

    @PostMapping(ServiceNameConstants.VOD_RPC_URL_PREFIX + "/media/url")
    Map<String, String> listMediaUrl(@RequestBody ListMediaUrlAO listMediaUrlAO);

    @PostMapping(ServiceNameConstants.VOD_RPC_URL_PREFIX + "/media/delete")
    R<Void> deleteMedia(@RequestBody DeleteMediaAO deleteMediaAO);

    @PostMapping(ServiceNameConstants.VOD_RPC_URL_PREFIX + "/media/detail")
    R<MediaInfoDTO> getMediaInfo(@RequestParam("fileId") String fileId);

    @PostMapping(ServiceNameConstants.VOD_RPC_URL_PREFIX + "/media/list")
    R<List<MediaInfoDTO>> listMediaInfo(@RequestBody ListMediaAO listMediaAO);
}
