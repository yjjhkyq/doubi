package com.x.provider.video.service.impl.recmmend;

import cn.hutool.core.util.StrUtil;
import com.x.core.utils.SpringUtils;
import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.api.statistic.constants.StatisticEventTopic;
import com.x.provider.api.statistic.enums.StatTotalItemNameEnum;
import com.x.provider.api.statistic.enums.StatisticObjectClassEnum;
import com.x.provider.api.statistic.enums.StatisticPeriodEnum;
import com.x.provider.api.statistic.model.event.StatisticTotalChangedEvent;
import com.x.provider.api.statistic.model.event.StatisticTotalEvent;
import com.x.provider.api.video.model.event.VideoPlayEvent;
import com.x.provider.video.configure.ApplicationConfig;
import com.x.provider.video.service.recmmend.VideoRecommendPoolService;
import com.x.provider.video.service.recmmend.VideoRecommendService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class VideoRecommendServiceImpl implements VideoRecommendService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationConfig applicationConfig;
    private final Collection<VideoRecommendPoolService> videoRecommendPoolServices;

    public VideoRecommendServiceImpl(KafkaTemplate<String, Object> kafkaTemplate,
                                     ApplicationConfig applicationConfig){
        this.kafkaTemplate = kafkaTemplate;
        this.applicationConfig = applicationConfig;
        this.videoRecommendPoolServices = SpringUtils.getBeansOfType(VideoRecommendPoolService.class).values();
    }

    @Override
    public void onStar(StarEvent starEvent) {
        if (starEvent.isStar() && starEvent.isFirstStar() && starEvent.getItemType().equals(StarItemTypeEnum.VIDEO.getValue())){
            incVideoScore(Long.parseLong(starEvent.getItemId()), 1L);
        }
    }

    @Override
    public void onPlay(VideoPlayEvent videoPlayEvent) {
        if ((int)(videoPlayEvent.getDuration() * applicationConfig.getVideoPlayFullMinRate()) <= videoPlayEvent.getPlayDuration()){
            incVideoScore(videoPlayEvent.getId(), 1L);
        }
    }

    @Override
    public void onStatisticTotalChanged(StatisticTotalChangedEvent statTotalChangedEvent) {
        if (statTotalChangedEvent.getStatisticObjectClassEnum().equals(StatisticObjectClassEnum.VIDEO.getValue())
                && statTotalChangedEvent.getStatTotalItemNameEnum().equals(StatTotalItemNameEnum.VIDEO_SCORE.getValue())
                && statTotalChangedEvent.getStatisticPeriodEnum().equals(StatisticPeriodEnum.ALL.getValue())){
            Long score = statTotalChangedEvent.getLongValue();
            Long videoId = Long.parseLong(statTotalChangedEvent.getStatisticObjectId());
            videoRecommendPoolServices.stream().forEach(item -> {
                item.onVideoScoreChanged(videoId, score);
            });
        }
    }

    @Override
    public void onVideoDeleted(Long videoId) {
        videoRecommendPoolServices.stream().forEach(item ->{
            item.onVideoDeleted(videoId);
        });
    }


    public void incVideoScore(Long videoId, Long score){
        kafkaTemplate.send(StatisticEventTopic.TOPIC_NAME_STAT_TOTAL_EVENT, StrUtil.format("{}:{}", StatisticObjectClassEnum.VIDEO.getValue(), videoId.toString()),
                StatisticTotalEvent.builder().longValue(score).statisticObjectClassEnum(StatisticObjectClassEnum.VIDEO.getValue())
                        .statisticObjectId(String.valueOf(videoId)).statisticPeriodEnum(StatisticPeriodEnum.ALL.getValue())
                        .statTotalItemNameEnum(StatTotalItemNameEnum.VIDEO_SCORE.getValue()).build());
    }
}
