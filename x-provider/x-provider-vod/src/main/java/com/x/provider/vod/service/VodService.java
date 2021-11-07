package com.x.provider.vod.service;

import com.x.provider.api.vod.model.ao.GetContentReviewResultAO;
import com.x.provider.api.vod.model.ao.ListMediaUrlAO;
import com.x.provider.vod.model.domain.MediaInfo;
import com.x.provider.vod.model.vo.VodUploadParamVO;
import com.tencentcloudapi.vod.v20180717.models.EventContent;

import java.util.Map;

public interface VodService {
    VodUploadParamVO getVodUploadParam(long customerId);
    void onEvent(EventContent eventContent);
    void contentReview(GetContentReviewResultAO getContentReviewResultAO);
    Map<String, String> listMediaUrl(ListMediaUrlAO getMediaUrlAO);
    void deleteMedia(String fileId);
    MediaInfo getMediaInfo(long id, String fileId);
}
