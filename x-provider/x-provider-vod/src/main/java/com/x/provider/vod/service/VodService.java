package com.x.provider.vod.service;

import com.x.provider.api.vod.model.ao.GetContentReviewResultAO;
import com.x.provider.vod.model.vo.VodUploadParamVO;
import com.tencentcloudapi.vod.v20180717.models.EventContent;

public interface VodService {
    VodUploadParamVO getVodUploadParam(long customerId);
    void onEvent(EventContent eventContent);
    void contentReview(GetContentReviewResultAO getContentReviewResultAO);
}
