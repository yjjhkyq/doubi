package com.x.provider.video.service;

import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.api.vod.model.dto.ContentReviewResultDTO;
import com.x.provider.video.model.ao.homepage.CreateVideoAO;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoTopic;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VideoService {
    long createVideo(CreateVideoAO createVideoAO, long customerId);
    void deleteVideo(long id);
    void onVodContentReview(ContentReviewResultDTO contentReviewResultDTO);
    void topMyVideo(long videoId, boolean top, long customerId);
    Optional<Video> getVideo(long videoId);
    PageList<Video> listVideo(long customerId, PageDomain pageDomain);
    List<Video> listVideo(List<Long> ids);
    void onStar(StarEvent starEvent);
    PageList<Video> listCustomerStarVideo(PageDomain pageDomain, long starCustomerId);
    List<Video> listVideo(List<Long> customerIds, Date afterUpdateDate);
    List<VideoTopic> listVideoTopic(Long videoId);
}
