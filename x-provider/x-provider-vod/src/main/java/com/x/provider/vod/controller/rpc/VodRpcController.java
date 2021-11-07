package com.x.provider.vod.controller.rpc;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.provider.api.vod.model.ao.DeleteMediaAO;
import com.x.provider.api.vod.model.ao.GetContentReviewResultAO;
import com.x.provider.api.vod.model.ao.ListMediaUrlAO;
import com.x.provider.api.vod.model.dto.MediaInfoDTO;
import com.x.provider.api.vod.service.VodRpcService;
import com.x.provider.vod.model.domain.MediaInfo;
import com.x.provider.vod.service.VodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/rpc/vod")
public class VodRpcController implements VodRpcService {

    private final VodService vodService;

    public VodRpcController(VodService vodService){
        this.vodService = vodService;
    }

    @Override
    @PostMapping("/media/url")
    public Map<String, String> listMediaUrl(@RequestBody ListMediaUrlAO listMediaUrlAO) {
        return vodService.listMediaUrl(listMediaUrlAO);
    }

    @PostMapping("/media/delete")
    @Override
    public R<Void> deleteMedia(@RequestBody DeleteMediaAO deleteMediaAO) {
        vodService.deleteMedia(deleteMediaAO.getFileId());
        return R.ok();
    }

    @PostMapping("/media/detail")
    @Override
    public R<MediaInfoDTO> getMediaInfo(String fileId) {
        MediaInfo mediaInfo = vodService.getMediaInfo(0, fileId);
        return R.ok(BeanUtil.prepare(mediaInfo, MediaInfoDTO.class));
    }

    @Override
    @PostMapping("/content/review")
    public R<Void> contentReview(@RequestBody GetContentReviewResultAO getContentReviewResultAO) {
        vodService.contentReview(getContentReviewResultAO);
        return R.ok();
    }

}
