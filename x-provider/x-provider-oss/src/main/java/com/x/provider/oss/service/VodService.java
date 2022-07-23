package com.x.provider.oss.service;

import com.tencentcloudapi.vod.v20180717.models.EventContent;
import com.x.provider.api.oss.model.ao.vod.GetContentReviewResultAO;
import com.x.provider.api.oss.model.ao.vod.ListMediaAO;
import com.x.provider.api.oss.model.ao.vod.ListMediaUrlAO;
import com.x.provider.oss.model.domain.MediaInfo;
import com.x.provider.oss.model.domain.MediaTranscodeItem;
import com.x.provider.oss.model.vo.vod.VodUploadParamVO;

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
