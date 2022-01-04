package com.x.provider.video.component;

import com.x.provider.api.finance.model.event.FinanceDataChangedEvent;
import com.x.provider.api.finance.model.event.FinanceDataChangedEventEnum;
import com.x.provider.api.general.constants.GeneralEventTopic;
import com.x.provider.api.general.model.event.CommentEvent;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.api.statistic.constants.StatisticEventTopic;
import com.x.provider.api.statistic.model.event.MetricValueChangedEvent;
import com.x.provider.api.video.constants.VideoEventTopic;
import com.x.provider.api.video.model.event.VideoChangedEvent;
import com.x.provider.api.video.model.event.VideoPlayEvent;
import com.x.provider.video.service.TopicService;
import com.x.provider.video.service.VideoMcService;
import com.x.provider.video.service.VideoService;
import com.x.provider.video.service.recmmend.VideoRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    private final TopicService topicService;
    private final VideoService videoService;
    private final VideoRecommendService videoRecommendService;
    private final VideoMcService videoMcService;

    public KafkaConsumer(TopicService topicService,
                         VideoService videoService,
                         VideoRecommendService videoRecommendService,
                         VideoMcService videoMcService){
        this.topicService = topicService;
        this.videoService = videoService;
        this.videoRecommendService = videoRecommendService;
        this.videoMcService = videoMcService;
    }

    @KafkaListener(topics = FinanceDataChangedEventEnum.TOPIC_NAME)
    public void receive(FinanceDataChangedEvent event) {
        topicService.onFinanceDataChanged(event);
    }

    @KafkaListener(topics = GeneralEventTopic.TOPIC_NAME_STAR)
    public void onStar(StarEvent event) {
        videoService.onStar(event);
        videoRecommendService.onStar(event);
        videoMcService.onStar(event);
    }

    @KafkaListener(topics = VideoEventTopic.TOPIC_NAME_VIDEO_PLAY)
    public void onVideoPlayEvent(VideoPlayEvent event) {
        videoRecommendService.onPlay(event);
    }

    @KafkaListener(topics = VideoEventTopic.TOPIC_NAME_VIDEO_CHANGED)
    public void oVideoChangedEvent(VideoChangedEvent event) {
        if (event.getEventType().equals(VideoChangedEvent.EventTypeEnum.VIDEO_DELETED.getValue())) {
            videoRecommendService.onVideoDeleted(event.getId());
        }
        videoMcService.onVideoChanged(event);
    }

    @KafkaListener(topics = StatisticEventTopic.TOPIC_NAME_STAT_METRIC_CHANGED_EVENT)
    public void onStatisticTotalChangedEvent(MetricValueChangedEvent event) {
        videoRecommendService.onStatisticTotalChanged(event);
    }

    @KafkaListener(topics = GeneralEventTopic.TOPIC_NAME_COMMENT)
    public void onCommentEvent(CommentEvent event) {
        videoMcService.onComment(event);
    }
}
