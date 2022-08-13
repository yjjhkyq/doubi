package com.x.provider.oss.service;

import com.tencentcloudapi.vod.v20180717.models.EventContent;
import com.x.provider.api.oss.model.dto.vod.GetContentReviewResultRequestDTO;
import com.x.provider.api.oss.model.dto.vod.ListMediaRequestDTO;
import com.x.provider.api.oss.model.dto.vod.ListMediaUrlRequestDTO;
import com.x.provider.oss.model.domain.MediaInfo;
import com.x.provider.oss.model.domain.MediaTranscodeItem;
import com.x.provider.oss.model.vo.vod.VodUploadParamVO;

import java.util.List;
import java.util.Map;

public interface VodService {
    VodUploadParamVO getVodUploadParam(long customerId, String fileName);
    void onEvent(EventContent eventContent);
    void contentReview(GetContentReviewResultRequestDTO getContentReviewResultAO);
    Map<String, String> listMediaUrl(ListMediaUrlRequestDTO getMediaUrlAO);
    void deleteMedia(String fileId);
    MediaInfo getMediaInfo(long id, String fileId);
    List<MediaInfo> listMediaInfo(ListMediaRequestDTO listMediaAO);
    List<MediaTranscodeItem> listMediaTranscodeItem(List<String> fileIdList);
}
