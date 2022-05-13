package com.x.provider.video.service;

import com.x.provider.video.enums.FollowVideoTypeEnum;
import com.x.provider.video.enums.VideoRecommendPoolEnum;

public interface RedisKeyService {
    //VideoPlayCount
    String getVideoPlayMetricKey(long videoId);
    String getVideoPlayMetricHashKey(long customerId);
    String getCustomerStarVideoKey(long customerId);
    String getMyFollowVideoKey(FollowVideoTypeEnum followVideoTypeEnum, long customerId);
    String getMyFollowVideoInitLockKey(FollowVideoTypeEnum followVideoTypeEnum, long customerId);
    String getMyFollowVideoInitTimeKey();
    String getMyFollowVideoInitTimeHashKey(FollowVideoTypeEnum followVideoTypeEnum, long customerId);
    String getVideoRecommendPoolKey(VideoRecommendPoolEnum videoRecommendPoolIdEnum);
    String getHotVideoByTopicId(long topicId);
}
