package com.x.provider.vod.service;

import com.x.provider.api.vod.model.ao.GetContentReviewResultAO;
import com.x.provider.api.vod.model.ao.ListMediaAO;
import com.x.provider.api.vod.model.ao.ListMediaUrlAO;
import com.x.provider.vod.model.domain.MediaInfo;
import com.x.provider.vod.model.domain.MediaTranscodeItem;
import com.x.provider.vod.model.vo.VodUploadParamVO;
import com.tencentcloudapi.vod.v20180717.models.EventContent;

import java.util.List;
import java.util.Map;

public interface VodService {
    VodUploadParamVO getVodUploadParam(long customerId, String fileName);
    void onEvent(EventContent eventContent);
    void contentReview(GetContentReviewResultAO getContentReviewResultAO);
    Map<String, String> listMediaUrl(ListMediaUrlAO getMediaUrlAO);
    void deleteMedia(String fileId);
    MediaInfo getMediaInfo(long id, String fileId);
    List<MediaInfo> listMediaInfo(ListMediaAO listMediaAO);
    List<MediaTranscodeItem> listMediaTranscodeItem(List<String> fileIdList);
}
