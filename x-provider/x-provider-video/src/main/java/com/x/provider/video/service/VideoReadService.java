package com.x.provider.video.service;

import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoRecommendPool;
import com.x.provider.video.model.domain.VideoRecommendPoolHotTopic;

import java.util.List;

public interface VideoReadService {
    List<Video> listMyFollowPersonVideo(Long customerId);
    List<Long> listMyFollowTopicHotVideo(Long customerId);
    List<VideoRecommendPool> listHotVideo();
    List<VideoRecommendPoolHotTopic> listHotVideoTopic(Long topicId);
    PageList<VideoRecommendPool> listScreenVideo(PageDomain pageDomain);
}
