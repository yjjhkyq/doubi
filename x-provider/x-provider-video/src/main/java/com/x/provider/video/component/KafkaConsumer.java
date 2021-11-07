package com.x.provider.video.component;

import com.x.provider.api.finance.model.event.FinanceDataChangedEvent;
import com.x.provider.api.finance.model.event.FinanceDataChangedEventEnum;
import com.x.provider.api.general.constants.GeneralEventTopic;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.api.statistic.constants.StatisticEventTopic;
import com.x.provider.api.statistic.model.event.StatisticTotalChangedEvent;
import com.x.provider.api.video.constants.VideoEventTopic;
import com.x.provider.api.video.model.event.VideoChangedEvent;
import com.x.provider.api.video.model.event.VideoPlayEvent;
import com.x.provider.video.service.TopicService;
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

    public KafkaConsumer(TopicService topicService,
                         VideoService videoService,
                         VideoRecommendService videoRecommendService){
        this.topicService = topicService;
        this.videoService = videoService;
        this.videoRecommendService = videoRecommendService;
    }

    @KafkaListener(topics = FinanceDataChangedEventEnum.TOPIC_NAME)
    public void receive(FinanceDataChangedEvent event) {
        topicService.onFinanceDataChanged(event);
    }

    @KafkaListener(topics = GeneralEventTopic.TOPIC_NAME_STAR)
    public void onStar(StarEvent event) {
        videoService.onStar(event);
        videoRecommendService.onStar(event);
    }

    @KafkaListener(topics = VideoEventTopic.TOPIC_NAME_VIDEO_PLAY)
    public void onVideoPlay(VideoPlayEvent event) {
        videoRecommendService.onPlay(event);
    }

    @KafkaListener(topics = VideoEventTopic.TOPIC_NAME_VIDEO_CHANGED)
    public void onVideoPlay(VideoChangedEvent event) {
        if (event.getEventType().equals(VideoChangedEvent.EventTypeEnum.VIDEO_DELETED.getValue())) {
            videoRecommendService.onVideoDeleted(event.getId());
        }
    }

    @KafkaListener(topics = StatisticEventTopic.TOPIC_NAME_STAT_TOTAL_CHANGED_EVENT)
    public void onVideoPlay(StatisticTotalChangedEvent event) {
        videoRecommendService.onStatisticTotalChanged(event);
    }
}
