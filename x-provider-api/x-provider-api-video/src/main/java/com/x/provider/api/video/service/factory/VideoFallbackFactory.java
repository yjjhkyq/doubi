package com.x.provider.api.video.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.video.model.ao.ListTopicAO;
import com.x.provider.api.video.model.dto.TopicDTO;
import com.x.provider.api.video.service.VideoRpcService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class VideoFallbackFactory implements FallbackFactory<VideoRpcService> {


    @Override
    public VideoRpcService create(Throwable throwable) {
        return new VideoRpcService() {
            @Override
            public R<List<TopicDTO>> listTopic(ListTopicAO listTopicAO) {
                return null;
            }
        };
    }
}
