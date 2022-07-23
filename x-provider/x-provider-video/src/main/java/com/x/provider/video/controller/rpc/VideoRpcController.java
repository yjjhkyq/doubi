package com.x.provider.video.controller.rpc;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.provider.api.oss.model.dto.vod.ContentReviewResultDTO;
import com.x.provider.api.video.model.ao.ListTopicAO;
import com.x.provider.api.video.model.dto.TopicDTO;
import com.x.provider.api.video.service.VideoRpcService;
import com.x.provider.video.model.domain.Topic;
import com.x.provider.video.service.TopicService;
import com.x.provider.video.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rpc/video")
public class VideoRpcController implements VideoRpcService {

    private final VideoService videoService;
    private final TopicService topicService;

    public VideoRpcController(VideoService videoService,
                              TopicService topicService){
        this.videoService = videoService;
        this.topicService = topicService;
    }

    @PostMapping("review/notify")
    public R<Void> onVodContentReview(@RequestBody ContentReviewResultDTO contentReviewResultDTO){
        videoService.onVodContentReview(contentReviewResultDTO);
        return R.ok();
    }

    @PostMapping("topic/list")
    @Override
    public R<List<TopicDTO>> listTopic(@RequestBody ListTopicAO listTopicAO) {
        List<Topic> topicList = topicService.listTopic(listTopicAO);
        return R.ok(BeanUtil.prepare(topicList, TopicDTO.class));
    }
}
