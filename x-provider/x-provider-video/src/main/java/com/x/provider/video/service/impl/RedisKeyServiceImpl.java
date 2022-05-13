package com.x.provider.video.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.provider.video.enums.FollowVideoTypeEnum;
import com.x.provider.video.enums.VideoRecommendPoolEnum;
import com.x.provider.video.service.RedisKeyService;
import org.springframework.stereotype.Service;

@Service
public class RedisKeyServiceImpl implements RedisKeyService {
    private static final String KEY_PREFIX = "Video";
    private static final String FULL_KEY = KEY_PREFIX + ":{}";

    private static final String VIDEO_PLAY_METRIC = "VideoPlayMetric:VideoId:{}";
    private static final String VIDEO_PLAY_METRIC_HASH = "PlayDuration:CustomerId:{}";
    private static final String VIDEO_STAR_CUSTOMER = "Video:Star:CustomerId:{}";
    private static final String VIDEO_MY_FOLLOW = "Video:My:Follow:{}:CustomerId:{}";
    private static final String VIDEO_MY_FOLLOW_INIT_LOCK_KEY = "Video:My:Follow:{}:CustomerId:{}";
    private static final String VIDEO_MY_FOLLOW_INIT_TIME = "Video:My:Follow:Init:Time";
    private static final String VIDEO_MY_FOLLOW_INIT_TIME_HASH = "CustomerId:{}:FollowVideoType:{}";
    private static final String VIDEO_RECOMMEND_POOL = "Video:RecommendPool:Id:{}";
    private static final String VIDEO_HOT_TOPIC_ID = "Video:Hot:Topic:Id:{}";

    private String getFullKey(CharSequence keyTemplate, Object... params){
        return StrUtil.format(FULL_KEY, StrUtil.format(keyTemplate, params));
    }

    @Override
    public String getVideoPlayMetricKey(long videoId) {
        return getFullKey(VIDEO_PLAY_METRIC, videoId);
    }

    @Override
    public String getVideoPlayMetricHashKey(long customerId) {
        return getFullKey(VIDEO_PLAY_METRIC_HASH, customerId);
    }

    @Override
    public String getCustomerStarVideoKey(long customerId) {
        return getFullKey(VIDEO_STAR_CUSTOMER, customerId);
    }

    @Override
    public String getMyFollowVideoKey(FollowVideoTypeEnum followVideoTypeEnum, long customerId) {
        return getFullKey(VIDEO_MY_FOLLOW, followVideoTypeEnum.name(), customerId);
    }

    @Override
    public String getMyFollowVideoInitLockKey(FollowVideoTypeEnum followVideoTypeEnum, long customerId) {
        return getFullKey(VIDEO_MY_FOLLOW_INIT_LOCK_KEY, followVideoTypeEnum.name(), customerId);
    }

    @Override
    public String getMyFollowVideoInitTimeKey() {
        return getFullKey(VIDEO_MY_FOLLOW_INIT_TIME);
    }

    @Override
    public String getMyFollowVideoInitTimeHashKey(FollowVideoTypeEnum followVideoTypeEnum, long customerId) {
        return getFullKey(VIDEO_MY_FOLLOW_INIT_TIME_HASH, customerId, followVideoTypeEnum.name());
    }

    @Override
    public String getVideoRecommendPoolKey(VideoRecommendPoolEnum videoRecommendPoolIdEnum) {
        return getFullKey(VIDEO_RECOMMEND_POOL, videoRecommendPoolIdEnum.name());
    }

    @Override
    public String getHotVideoByTopicId(long topicId) {
        return getFullKey(VIDEO_HOT_TOPIC_ID, topicId);
    }
}
