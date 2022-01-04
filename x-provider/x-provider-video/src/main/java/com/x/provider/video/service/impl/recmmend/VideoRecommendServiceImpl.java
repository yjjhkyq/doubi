package com.x.provider.video.service.impl.recmmend;

import cn.hutool.core.util.StrUtil;
import com.x.core.utils.SpringUtils;
import com.x.provider.api.common.enums.ItemTypeEnum;
import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.api.statistic.constants.StatisticEventTopic;
import com.x.provider.api.statistic.enums.MetricEnum;
import com.x.provider.api.statistic.enums.PeriodEnum;
import com.x.provider.api.statistic.model.event.IncMetricValueEvent;
import com.x.provider.api.statistic.model.event.MetricValueChangedEvent;
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
    public void onStatisticTotalChanged(MetricValueChangedEvent statTotalChangedEvent) {
        if (statTotalChangedEvent.getItemTypeEnum().equals(ItemTypeEnum.VIDEO.getValue())
                && statTotalChangedEvent.getMetricEnum().equals(MetricEnum.SCORE.getValue())
                && statTotalChangedEvent.getPeriodEnum().equals(PeriodEnum.ALL.getValue())){
            Long score = statTotalChangedEvent.getLongValue();
            Long videoId = Long.parseLong(statTotalChangedEvent.getItemId());
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
        kafkaTemplate.send(StatisticEventTopic.TOPIC_NAME_STAT_INC_METRIC_VALUE_EVENT, StrUtil.format("{}:{}", ItemTypeEnum.VIDEO.getValue(), videoId.toString()),
                IncMetricValueEvent.builder().longValue(score).itemType(ItemTypeEnum.VIDEO.getValue())
                        .itemId(String.valueOf(videoId)).periodEnum(PeriodEnum.ALL.getValue())
                        .metricEnum(MetricEnum.SCORE.getValue()).build());
    }
}
