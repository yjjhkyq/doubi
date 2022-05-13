package com.x.provider.vod.controller.rpc;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.provider.api.vod.model.ao.DeleteMediaAO;
import com.x.provider.api.vod.model.ao.GetContentReviewResultAO;
import com.x.provider.api.vod.model.ao.ListMediaAO;
import com.x.provider.api.vod.model.ao.ListMediaUrlAO;
import com.x.provider.api.vod.model.dto.MediaInfoDTO;
import com.x.provider.api.vod.service.VodRpcService;
import com.x.provider.vod.model.domain.MediaInfo;
import com.x.provider.vod.model.domain.MediaTranscodeItem;
import com.x.provider.vod.service.VodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return R.ok(prepare(mediaInfo));
    }

    @PostMapping("/media/list")
    @Override
    public R<List<MediaInfoDTO>> listMediaInfo(@RequestBody ListMediaAO listMediaAO) {
        List<MediaInfo> result = vodService.listMediaInfo(listMediaAO);
        return R.ok(prepare(result));
    }

    @Override
    @PostMapping("/content/review")
    public R<Void> contentReview(@RequestBody GetContentReviewResultAO getContentReviewResultAO) {
        vodService.contentReview(getContentReviewResultAO);
        return R.ok();
    }

    private MediaInfoDTO prepare(MediaInfo mediaInfo){
        return prepare(Arrays.asList(mediaInfo)).stream().findFirst().get();
    }

    private List<MediaInfoDTO> prepare(List<MediaInfo> source){
        if (source.isEmpty()){
            return new ArrayList<>();
        }
        Map<String, MediaTranscodeItem> transcodeItemMap = vodService.listMediaTranscodeItem(source.stream().map(item -> item.getFileId()).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(item -> item.getFileId(), item -> item));
        List<MediaInfoDTO> result = BeanUtil.prepare(source, MediaInfoDTO.class);
        result.forEach(item -> {
            if (transcodeItemMap.containsKey(item.getFileId())){
                item.setVodUrl(transcodeItemMap.get(item.getFileId()).getUrl());
            }

        });
        return result;
    }
}
