package com.x.provider.video.service;

import com.x.provider.api.vod.model.dto.ContentReviewResultDTO;
import com.x.provider.video.model.ao.CreateVideoAO;

public interface VideoService {
    long createVideo(CreateVideoAO createVideoAO, long customerId);
    void deleteVideo(long id);
    void onVodContentReview(ContentReviewResultDTO contentReviewResultDTO);
}
