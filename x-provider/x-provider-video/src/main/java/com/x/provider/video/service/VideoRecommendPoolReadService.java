package com.x.provider.video.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.web.page.CursorPageRequest;
import com.x.provider.video.model.domain.VideoRecommendPool;

public interface VideoRecommendPoolReadService {
    IPage<VideoRecommendPool> listScreen(CursorPageRequest cursorPageRequest);
    IPage<VideoRecommendPool> listHot(IPage page);
}
