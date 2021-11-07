package com.x.provider.video.service;

import com.x.core.web.page.CursorList;
import com.x.core.web.page.CursorPageRequest;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoRecommendPool;
import com.x.provider.video.model.domain.VideoRecommendPoolHotTopic;

import java.util.List;

public interface VideoReadService {
    List<Video> listMyFollowPersonVideo(Long customerId);
    List<Long> listMyFollowTopicHotVideo(Long customerId);
    List<VideoRecommendPool> listHotVideo();
    List<VideoRecommendPoolHotTopic> listHotVideoTopic(Long topicId);
    CursorList<Long> listScreenVideo(CursorPageRequest cursorPageRequest);
}
