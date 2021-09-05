package com.x.provider.video.controller.frontent;

import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.provider.video.model.ao.CreateVideoAO;
import com.x.provider.video.model.ao.TopicSearchAO;
import com.x.provider.video.model.vo.TopicSearchItemVO;
import com.x.provider.video.service.TopicService;
import com.x.provider.video.service.VideoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/frontend/video")
public class VideoController extends BaseFrontendController {

    private final VideoService videoService;

    public VideoController(VideoService videoService){
        this.videoService = videoService;
    }

    @PostMapping("/create")
    public R<Long> createVideo(@RequestBody CreateVideoAO createVideoAO){
        var videoId = videoService.createVideo(createVideoAO, getCurrentCustomerId());
        return R.ok(videoId);
    }

    @PostMapping("/delete")
    public R<Void> deleteVideo(long id){
        videoService.deleteVideo(id);
        return R.ok();
    }
}
