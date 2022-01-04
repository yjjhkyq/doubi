package com.x.provider.video.service.impl;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.provider.api.common.enums.ItemTypeEnum;
import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.general.model.ao.StarAO;
import com.x.provider.api.general.model.dto.ItemStatisticDTO;
import com.x.provider.api.general.service.ItemRpcService;
import com.x.provider.api.general.service.StarRpcService;
import com.x.provider.api.statistic.enums.MetricEnum;
import com.x.provider.api.statistic.enums.PeriodEnum;
import com.x.provider.api.statistic.model.ao.IncMetricValueValueAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueBatchAO;
import com.x.provider.api.statistic.model.dto.ListMetricValueDTO;
import com.x.provider.api.statistic.service.StatisticTotalRpcService;
import com.x.provider.api.video.constants.VideoEventTopic;
import com.x.provider.api.video.enums.VideoStatusEnum;
import com.x.provider.api.video.model.event.VideoPlayEvent;
import com.x.provider.video.mapper.VideoPlayMetricMapper;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoPlayMetric;
import com.x.provider.video.model.domain.VideoStatistic;
import com.x.provider.video.service.RedisKeyService;
import com.x.provider.video.service.VideoMetricService;
import com.x.provider.video.service.VideoService;
import com.x.redis.service.RedisService;
import com.x.util.StringUtil;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoMetricServiceImpl implements VideoMetricService {

    private final RedisKeyService redisKeyService;
    private final RedisService redisService;
    private final VideoPlayMetricMapper videoPlayMetricMapper;
    private final VideoService videoService;
    private final StatisticTotalRpcService statisticTotalRpcService;
    private final StarRpcService starRpcService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ItemRpcService itemRpcService;

    public VideoMetricServiceImpl(RedisKeyService redisKeyService,
                                  RedisService redisService,
                                  VideoPlayMetricMapper videoPlayMetricMapper,
                                  VideoService videoService,
                                  StatisticTotalRpcService statisticTotalRpcService,
                                  StarRpcService starRpcService,
                                  KafkaTemplate<String, Object> kafkaTemplate,
                                  ItemRpcService itemRpcService){
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
        this.videoPlayMetricMapper = videoPlayMetricMapper;
        this.videoService = videoService;
        this.statisticTotalRpcService = statisticTotalRpcService;
        this.starRpcService = starRpcService;
        this.kafkaTemplate = kafkaTemplate;
        this.itemRpcService = itemRpcService;
    }

    @Override
    public Optional<VideoPlayMetric> getVideoPlayMetric(long videoId, long customerId) {
        return getVideoPlayMetricCache(videoId, customerId);
    }

    private Optional<VideoPlayMetric> getVideoPlayMetricCache(long videoId, long customerId) {
        Optional<Integer> playDurationOpt = redisService.getCacheMapValueOptional(redisKeyService.getVideoPlayMetricKey(videoId), redisKeyService.getVideoPlayMetricHashKey(customerId));
        if (playDurationOpt.isEmpty()){
            return Optional.empty();
        }
        return Optional.ofNullable(VideoPlayMetric.builder().videoId(videoId).customerId(customerId).playDuration(playDurationOpt.get()).build());
    }

    @Override
    public void reportVideoPlayMetric(long videoId, long customerId, int playDuration) {
        if (customerId <= 0){
            return;
        }
        Optional<VideoPlayMetric> videoPlayMetricOptional = getVideoPlayMetricCache(videoId, customerId);
        if (videoPlayMetricOptional.isPresent() && videoPlayMetricOptional.get().getPlayDuration() >= playDuration){
            return;
        }

        if (videoPlayMetricOptional.isPresent()){
            videoPlayMetricMapper.updateById(VideoPlayMetric.builder().id(videoPlayMetricOptional.get().getId()).playDuration(playDuration).build());
            savePlayDuration(videoId, customerId, playDuration);
            return;
        }
        Optional<Video> video = videoService.getVideo(videoId);
        if (video.isEmpty() || !video.get().getVideoStatus().equals(VideoStatusEnum.PUBLISH.ordinal())){
            return;
        }
        statisticTotalRpcService.incStatisticTotal(IncMetricValueValueAO.builder().longValue(1L).itemTypeEnum(ItemTypeEnum.VIDEO.getValue())
                .itemId(String.valueOf(videoId)).periodEnum(PeriodEnum.ALL.getValue()).metricEnum(MetricEnum.PLAY_COUNT.getValue()).build());
        savePlayDuration(videoId, customerId, playDuration);
        videoPlayMetricMapper.insert(VideoPlayMetric.builder().customerId(customerId).playDuration(playDuration).videoDuration((int)video.get().getDuration()).videoId(videoId).build());
        VideoPlayEvent videoPlayEvent = BeanUtil.prepare(video, VideoPlayEvent.class);
        videoPlayEvent.setPlayDuration(playDuration);
        kafkaTemplate.send(VideoEventTopic.TOPIC_NAME_VIDEO_PLAY, video.get().getId().toString(), videoPlayEvent);
    }

    @Override
    public void star(long starCustomerId, long starVideoId, boolean star) {
        Optional<Video> video = videoService.getVideo(starVideoId);
        if (video.isEmpty()){
            return;
        }
        starRpcService.star(StarAO.builder().itemId(video.get().getId())
                .itemType( StarItemTypeEnum.VIDEO.getValue()).starCustomerId(starCustomerId).star(star).build());
    }

    @Override
    public Map<Long, VideoStatistic> listVideoStatisticMap(List<Long> videoIdList) {
        List<ListMetricValueAO> conditions = new ArrayList<>();
        conditions.add(ListMetricValueAO.builder()
                .itemIds(videoIdList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemTypeEnum(ItemTypeEnum.VIDEO.getValue())
                .metricEnum(MetricEnum.COUNT.getValue())
                .periodEnum(PeriodEnum.ALL.getValue())
                .build());
        ListMetricValueBatchAO listMetricValueBatchAO = new ListMetricValueBatchAO();
        listMetricValueBatchAO.setConditions(conditions);
        List<ListMetricValueDTO> metricValues = statisticTotalRpcService.listStatisticTotalBatch(listMetricValueBatchAO).getData();
        Map<Long, VideoStatistic> result = prepare(metricValues);
        Map<Long, ItemStatisticDTO> itemStatMap = itemRpcService.listStatMap(ItemTypeEnum.VIDEO.getValue(), StringUtil.toString(videoIdList)).getData();
        itemStatMap.entrySet().forEach(item -> {
            VideoStatistic videoStatistic = result.getOrDefault(item.getKey(), new VideoStatistic());
            videoStatistic.setStarCount(item.getValue().getStarCount());
            videoStatistic.setCommentCount(item.getValue().getCommentCount());
            result.putIfAbsent(item.getKey(), videoStatistic);
        });
        return result;
    }

    private void savePlayDuration(long videoId, long customerId, int playDuration){
        redisService.setCacheMapValue(redisKeyService.getVideoPlayMetricKey(videoId), redisKeyService.getVideoPlayMetricHashKey(customerId), playDuration);
    }

    private Map<Long, VideoStatistic> prepare(List<ListMetricValueDTO> metricValues){
        if (metricValues.isEmpty()){
            return new HashMap<>();
        }
        Map<Long, VideoStatistic> result = new HashMap<>(32);
        metricValues.stream().forEach(item -> {
            VideoStatistic videoStatistic = result.getOrDefault(Long.valueOf(item.getItemId()), VideoStatistic.builder().id(Long.valueOf(item.getItemId())).build());
            if (item.getMetricEnum() == MetricEnum.PLAY_COUNT.getValue()){
                videoStatistic.setPlayCount(item.getLongValue());
            }
            result.putIfAbsent(videoStatistic.getId(), videoStatistic);
        });
        return result;
    }
}
