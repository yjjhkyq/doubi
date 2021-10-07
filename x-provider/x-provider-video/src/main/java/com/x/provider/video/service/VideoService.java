package com.x.provider.video.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.x.core.web.page.PageDomain;
import com.x.provider.api.vod.model.dto.ContentReviewResultDTO;
import com.x.provider.video.model.ao.CreateVideoAO;
import com.x.provider.video.model.domain.Video;

import java.util.List;

public interface VideoService {
    long createVideo(CreateVideoAO createVideoAO, long customerId);
    void deleteVideo(long id);
    void onVodContentReview(ContentReviewResultDTO contentReviewResultDTO);
    void topMyVideo(long videoId, boolean top, long customerId);
    IPage<Video> listVideo(long customerId, IPage page);
    void star(long starCustomerId, long starVideoId, boolean star);
}
