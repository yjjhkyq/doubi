package com.x.provider.video.service;

import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.video.model.domain.VideoRecommendPool;

public interface VideoRecommendPoolReadService {
    PageList<VideoRecommendPool> listScreen(PageDomain page);
    PageList<VideoRecommendPool> listHot(PageDomain page);
}
