package com.x.provider.video.service;

import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoRecommendPool;
import com.x.provider.video.model.domain.VideoRecommendPoolHotTopic;

import java.util.List;

public interface VideoReadService {
    PageList<Video> listMyFollowPersonVideo(PageDomain pageDomain, Long customerId);
    PageList<Video> listMyFollowTopicHotVideo(PageDomain pageDomain, Long customerId);
    PageList<Video> listHotVideo(PageDomain pageDomain);
    PageList<Video> listHotVideoTopic(PageDomain pageDomain, Long topicId);
    PageList<Video> listScreenVideo(PageDomain pageDomain);
}
