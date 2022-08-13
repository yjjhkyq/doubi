package com.x.provider.api.video.service;

import com.x.core.web.api.R;
import com.x.provider.api.video.constants.ServiceNameConstants;
import com.x.provider.api.video.model.dto.ListTopicRequestDTO;
import com.x.provider.api.video.model.dto.TopicDTO;
import com.x.provider.api.video.service.factory.VideoFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "videoService", value = ServiceNameConstants.SERVICE, fallbackFactory = VideoFallbackFactory.class)
public interface VideoRpcService {

    @PostMapping(ServiceNameConstants.RPC_URL_PREFIX + "/topic/list")
    R<List<TopicDTO>> listTopic(@RequestBody ListTopicRequestDTO listTopicAO);
}
