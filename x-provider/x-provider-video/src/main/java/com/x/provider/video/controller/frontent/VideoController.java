package com.x.provider.video.controller.frontent;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.core.web.page.TableDataInfo;
import com.x.core.web.page.TableSupport;
import com.x.provider.api.vod.enums.MediaTypeEnum;
import com.x.provider.api.vod.model.ao.ListMediaUrlAO;
import com.x.provider.api.vod.service.VodRpcService;
import com.x.provider.video.model.ao.CreateVideoAO;
import com.x.provider.video.model.ao.StarVideoAO;
import com.x.provider.video.model.ao.TopMyVideoAO;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.vo.homepage.VideoItemList;
import com.x.provider.video.service.VideoService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.stream.Collectors;

@ApiModel(value = "视频服务")
@RestController
@RequestMapping("/frontend/video")
public class VideoController extends BaseFrontendController {

    private final VideoService videoService;
    private final VodRpcService vodRpcService;

    public VideoController(VideoService videoService,
                           VodRpcService vodRpcService){
        this.videoService = videoService;
        this.vodRpcService = vodRpcService;
    }

    @ApiOperation(value = "创建视频")
    @PostMapping("/create")
    public R<Long> createVideo(@RequestBody @Validated CreateVideoAO createVideoAO){
        var videoId = videoService.createVideo(createVideoAO, getCurrentCustomerId());
        return R.ok(videoId);
    }

    @ApiOperation(value = "删除视频")
    @PostMapping("/delete")
    public R<Void> deleteVideo(long id){
        videoService.deleteVideo(id);
        return R.ok();
    }

    @ApiOperation(value = "置顶视频")
    @PostMapping("/top")
    public R<Void> topMyVideo(@RequestBody @Validated TopMyVideoAO topMyVideoAO){
        videoService.topMyVideo(topMyVideoAO.getVideoId(), topMyVideoAO.isTop(), getCurrentCustomerId());
        return R.ok();
    }

    @ApiOperation(value = "点赞取消点赞视频")
    @PostMapping("/star")
    public R<Void> starVideo(@RequestBody @Validated StarVideoAO starVideoAO){
        videoService.star(getCurrentCustomerId(), starVideoAO.getVideoId(), starVideoAO.isStar());
        return R.ok();
    }

    @GetMapping("/list")
    public R<TableDataInfo> list(@Min(1) long customerId){
        IPage<Video> videos = videoService.listVideo(getCurrentCustomerId(), TableSupport.buildIPageRequest());
        if (videos.getSize() == 0){
            return R.ok(new TableDataInfo());
        }
        var coverUrls = new HashMap<String, String>((int)videos.getSize());
        var mediaUrls =vodRpcService.listMediaUrl(ListMediaUrlAO.builder().fileIds(videos.getRecords().stream().map(Video::getFileId).collect(Collectors.toList()))
                .mediaType(MediaTypeEnum.COVER).build());
        return R.ok(TableSupport.buildTableDataInfo(videos, (item) -> VideoItemList.builder().coverUrl(mediaUrls.get(item.getFileId())).id(item.getId()).top(item.getTop())
                .videoStatus(item.getVideoStatus()).build()));
    }

}
