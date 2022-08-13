package com.x.provider.video.service.impl;

import com.x.core.utils.BeanUtil;
import com.x.provider.api.common.enums.ItemTypeEnum;
import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.general.model.dto.StarRequestDTO;
import com.x.provider.api.general.service.ItemRpcService;
import com.x.provider.api.general.service.StarRpcService;
import com.x.provider.api.statistic.enums.MetricEnum;
import com.x.provider.api.statistic.enums.PeriodEnum;
import com.x.provider.api.statistic.model.dto.IncMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueBatchRequestDTO;
import com.x.provider.api.statistic.model.dto.MetricValueDTO;
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
        Optional<Integer> playDurationOpt = redisService.getCacheMapValueOptional(redisKeyService.getVideoPlayMetricKey(videoId), redisKeyService.getVideoPlayMetricHashKey(customerId), Integer.class);
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
        statisticTotalRpcService.incStatisticTotal(IncMetricValueRequestDTO.builder().longValue(1L).itemType(ItemTypeEnum.VIDEO.getValue())
                .itemId(String.valueOf(videoId)).period(PeriodEnum.ALL.getValue()).metric(MetricEnum.PLAY_COUNT.getValue()).build());
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
        starRpcService.star(StarRequestDTO.builder().itemId(video.get().getId())
                .itemType( StarItemTypeEnum.VIDEO.getValue()).starCustomerId(starCustomerId).star(star).build());
    }

    @Override
    public Map<Long, VideoStatistic> listVideoStatisticMap(List<Long> videoIdList) {
        List<ListMetricValueRequestDTO> conditions = new ArrayList<>();
        conditions.add(ListMetricValueRequestDTO.builder()
                .itemIds(videoIdList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemType(ItemTypeEnum.VIDEO.getValue())
                .metric(MetricEnum.PLAY_COUNT.getValue())
                .period(PeriodEnum.ALL.getValue())
                .build());
        conditions.add(ListMetricValueRequestDTO.builder()
                .itemIds(videoIdList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemType(ItemTypeEnum.VIDEO.getValue())
                .metric(MetricEnum.COMMENT_COUNT.getValue())
                .period(PeriodEnum.ALL.getValue())
                .build());
        conditions.add(ListMetricValueRequestDTO.builder()
                .itemIds(videoIdList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemType(ItemTypeEnum.VIDEO.getValue())
                .metric(MetricEnum.STAR_COUNT.getValue())
                .period(PeriodEnum.ALL.getValue())
                .build());
        ListMetricValueBatchRequestDTO listMetricValueBatchAO = new ListMetricValueBatchRequestDTO();
        listMetricValueBatchAO.setConditions(conditions);
        List<MetricValueDTO> metricValues = statisticTotalRpcService.listStatisticTotalBatch(listMetricValueBatchAO).getData();
        Map<Long, VideoStatistic> result = prepare(metricValues);
//        Map<Long, ItemStatisticDTO> itemStatMap = itemRpcService.listStatMap(ItemTypeEnum.VIDEO.getValue(), StringUtil.toString(videoIdList)).getData();
//        itemStatMap.entrySet().forEach(item -> {
//            VideoStatistic videoStatistic = result.getOrDefault(item.getKey(), new VideoStatistic());
//            videoStatistic.setStarCount(item.getValue().getStarCount());
//            videoStatistic.setCommentCount(item.getValue().getCommentCount());
//            result.putIfAbsent(item.getKey(), videoStatistic);
//        });
        return result;
    }

    private void savePlayDuration(long videoId, long customerId, int playDuration){
        redisService.setCacheMapValue(redisKeyService.getVideoPlayMetricKey(videoId), redisKeyService.getVideoPlayMetricHashKey(customerId), playDuration);
    }

    private Map<Long, VideoStatistic> prepare(List<MetricValueDTO> metricValues){
        if (metricValues.isEmpty()){
            return new HashMap<>();
        }
        Map<Long, VideoStatistic> result = new HashMap<>(32);
        metricValues.stream().forEach(item -> {
            VideoStatistic videoStatistic = result.getOrDefault(Long.valueOf(item.getItemId()), VideoStatistic.builder().id(Long.valueOf(item.getItemId())).build());
            if (item.getMetric() == MetricEnum.PLAY_COUNT.getValue()){
                videoStatistic.setPlayCount(item.getLongValue());
            }
            if (item.getMetric() == MetricEnum.COMMENT_COUNT.getValue()){
                videoStatistic.setCommentCount(item.getLongValue());
            }
            if (item.getMetric() == MetricEnum.COMMENT_COUNT.getValue()){
                videoStatistic.setCommentCount(item.getLongValue());
            }
            result.putIfAbsent(videoStatistic.getId(), videoStatistic);
        });
        return result;
    }
}
