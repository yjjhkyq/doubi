package com.x.provider.video.controller.rpc;

import com.x.core.web.api.R;
import com.x.provider.api.vod.model.dto.ContentReviewResultDTO;
import com.x.provider.video.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rpc/video")
public class VideoRpcController {

    private final VideoService videoService;

    public VideoRpcController(VideoService videoService){
        this.videoService = videoService;
    }

    @PostMapping("review/notify")
    public R<Void> onVodContentReview(@RequestBody ContentReviewResultDTO contentReviewResultDTO){
        videoService.onVodContentReview(contentReviewResultDTO);
        return R.ok();
    }
}
