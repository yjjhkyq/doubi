package com.x.provider.video.controller.frontent;

import com.x.core.exception.ApiException;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.core.web.page.PageHelper;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.general.enums.CommentItemTypeEnum;
import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.general.model.ao.CommentAO;
import com.x.provider.api.general.model.ao.StarAO;
import com.x.provider.api.general.service.CommentRpcService;
import com.x.provider.api.general.service.StarRpcService;
import com.x.provider.api.oss.service.OssRpcService;
import com.x.provider.api.statistic.service.StatisticTotalRpcService;
import com.x.provider.api.video.enums.VideoStatusEnum;
import com.x.provider.api.vod.enums.MediaTypeEnum;
import com.x.provider.api.vod.model.ao.ListMediaUrlAO;
import com.x.provider.api.vod.service.VodRpcService;
import com.x.provider.video.enums.VideoErrorEnum;
import com.x.provider.video.model.ao.ReportVideoPlayMetricAO;
import com.x.provider.video.model.ao.StarVideoAO;
import com.x.provider.video.model.ao.VideoCommentAO;
import com.x.provider.video.model.ao.VideoCommentStarAO;
import com.x.provider.video.model.ao.homepage.CreateVideoAO;
import com.x.provider.video.model.ao.homepage.TopMyVideoAO;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoRecommendPool;
import com.x.provider.video.model.domain.VideoRecommendPoolHotTopic;
import com.x.provider.video.model.domain.VideoStatistic;
import com.x.provider.video.model.vo.VideoStatisticVO;
import com.x.provider.video.model.vo.VideoVO;
import com.x.provider.video.service.VideoMetricService;
import com.x.provider.video.service.VideoReadService;
import com.x.provider.video.service.VideoService;
import com.x.util.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@ApiModel(value = "视频服务")
@RestController
@RequestMapping("/frontend/video")
public class VideoController extends BaseFrontendController {

    private final VideoService videoService;
    private final VodRpcService vodRpcService;
    private final StatisticTotalRpcService statisticTotalRpcService;
    private final CustomerRpcService customerRpcService;
    private final VideoMetricService videoMetricService;
    private final CommentRpcService commentRpcService;
    private final StarRpcService starRpcService;
    private final VideoReadService videoRecommendService;

    public VideoController(VideoService videoService,
                           VodRpcService vodRpcService,
                           StatisticTotalRpcService statisticTotalRpcService,
                           CustomerRpcService customerRpcService,
                           @Qualifier("videoDefaultExecutor") Executor executor,
                           OssRpcService ossRpcService,
                           VideoMetricService videoMetricService,
                           CommentRpcService commentRpcService,
                           StarRpcService starRpcService,
                           VideoReadService videoRecommendService){
        this.videoService = videoService;
        this.vodRpcService = vodRpcService;
        this.statisticTotalRpcService = statisticTotalRpcService;
        this.customerRpcService = customerRpcService;
        this.videoMetricService = videoMetricService;
        this.commentRpcService = commentRpcService;
        this.starRpcService = starRpcService;
        this.videoRecommendService = videoRecommendService;
    }

    @ApiOperation(value = "创建视频")
    @PostMapping("/homepage/create")
    public R<Long> createVideo(@RequestBody @Validated CreateVideoAO createVideoAO){
        var videoId = videoService.createVideo(createVideoAO, getCurrentCustomerId());
        return R.ok(videoId);
    }

    @ApiOperation(value = "删除视频")
    @PostMapping("/homepage/delete")
    public R<Void> deleteVideo(long id){
        videoService.deleteVideo(id);
        return R.ok();
    }

    @ApiOperation(value = "置顶视频")
    @PostMapping("/homepage/top")
    public R<Void> topMyVideo(@RequestBody @Validated TopMyVideoAO topMyVideoAO){
        videoService.topMyVideo(topMyVideoAO.getVideoId(), topMyVideoAO.isTop(), getCurrentCustomerId());
        return R.ok();
    }

    @ApiOperation(value = "视频详情")
    @GetMapping("/homepage/detail")
    public R<VideoVO> detail(@RequestParam  long id) throws InterruptedException {
        Video video = videoService.getVideo(id).orElseThrow(() -> new ApiException(VideoErrorEnum.VIDEO_NOT_EXISTED));
        long currentCustomerId =  getCurrentCustomerId();
        return R.ok(prepare(currentCustomerId, Arrays.asList(video)).stream().findFirst().get());
    }

    @ApiOperation(value = "我的视频")
    @GetMapping("/homepage/list")
    public R<PageList<VideoVO>> list(@RequestParam(required = false, defaultValue = "0") long cursor,
                                         @RequestParam int pageSize,
                                         @RequestParam @Min(1) long customerId){
        PageList<Video> videos = videoService.listVideo(customerId, PageHelper.getPageDomain());
        if (videos.isEmptyList()){
            return R.ok(new PageList());
        }
        return R.ok(PageList.map(videos, prepare(getCurrentCustomerId(), videos.getList())));
    }

    @ApiOperation(value = "点赞/取消点赞视频")
    @PostMapping("/star")
    public R<Void> starVideo(@RequestBody @Validated StarVideoAO starVideoAO){
        videoMetricService.star(getCurrentCustomerId(), starVideoAO.getVideoId(), starVideoAO.isStar());
        return R.ok();
    }

    @ApiOperation(value = "点赞视频列表")
    @GetMapping("/star/list")
    public R<PageList<VideoVO>> customerStarVideoList(@RequestParam(required = false, defaultValue = "0") long cursor,
                                                      @RequestParam int pageSize,
                                                      @RequestParam @Min(1) long customerId){
        PageList<Video> videos = videoService.listCustomerStarVideo(PageHelper.getPageDomain(), customerId);
        if (videos.isEmptyList()){
            return R.ok(new PageList());
        }
        List<VideoVO> videoList = prepare(getCurrentCustomerId(), videos.getList());
        return R.ok(PageList.map(videos, videoList));
    }

    @ApiOperation(value = "上报视频实际播放时长")
    @PostMapping("/metric/play/duration/report")
    public R<Void> reportVideoPlayMetric(@RequestBody @Validated ReportVideoPlayMetricAO reportVideoPlayMetricAO){
        videoMetricService.reportVideoPlayMetric(reportVideoPlayMetricAO.getVideoId(), getCurrentCustomerIdAndNotCheckLogin(), reportVideoPlayMetricAO.getPlayDuration());
        return R.ok();
    }

    @ApiOperation(value = "评论视频")
    @PostMapping("/comment")
    public R<Void> videoComment(@RequestBody @Validated VideoCommentAO videoCommentAO){
        Optional<Video> video = videoService.getVideo(videoCommentAO.getItemId());
        ApiAssetUtil.isTrue(video.isPresent(), VideoErrorEnum.VIDEO_NOT_EXISTED);
        if(!video.get().getVideoStatus().equals(VideoStatusEnum.PUBLISH.ordinal())){
            return R.ok();
        }
        return commentRpcService.comment(CommentAO.builder()
                .itemCustomerId(video.get().getCustomerId())
                .parentCommentId(videoCommentAO.getParentCommentId())
                .commentCustomerId(getCurrentCustomerId())
                .content(videoCommentAO.getContent())
                .itemType(CommentItemTypeEnum.VIDEO.getValue())
                .itemId(videoCommentAO.getItemId())
                .build());
    }

    @ApiOperation(value = "点赞/取消点赞评论")
    @PostMapping("/comment/star")
    public R<Boolean> videoCommentStar(@RequestBody @Validated VideoCommentStarAO videoCommentStarAO){
        Optional<Video> video = videoService.getVideo(videoCommentStarAO.getVideoId());
        ApiAssetUtil.isTrue(video.isPresent(), VideoErrorEnum.VIDEO_NOT_EXISTED);
        if(!video.get().getVideoStatus().equals(VideoStatusEnum.PUBLISH.ordinal())){
            return R.ok();
        }
        return starRpcService.star(StarAO.builder().itemId(videoCommentStarAO.getCommentId()).itemType(StarItemTypeEnum.COMMENT.getValue())
                .star(videoCommentStarAO.isStar()).starCustomerId(getCurrentCustomerId()).build());
    }

    @ApiOperation(value = "我关注的人发布的视频,返回值为视频id列表")
    @GetMapping("/my/follow/person")
    public R<List<Long>> listMyFollowPersonVideo(){
        return R.ok(videoRecommendService.listMyFollowPersonVideo(getCurrentCustomerId()).stream().map(Video::getId).collect(Collectors.toList()));
    }

    @ApiOperation(value = "我的自选主题下的热门视频")
    @GetMapping("/hot/my/follow/topic")
    public R<List<Long>> listMyFollowTopicHotVideo(){
        return R.ok(videoRecommendService.listMyFollowTopicHotVideo(getCurrentCustomerId()));
    }

    @ApiOperation(value = "热门视频")
    @GetMapping("/hot/")
    public R<List<Long>> listHotVideo(){
        return R.ok(videoRecommendService.listHotVideo().stream().map(VideoRecommendPool::getVideoId).collect(Collectors.toList()));
    }

    @ApiOperation(value = "主题下的热门视频")
    @GetMapping("/hot/topic")
    public R<List<Long>> listHotVideoTopic(@ApiParam("主题id") @RequestParam Long topicId){
        return R.ok(videoRecommendService.listHotVideoTopic(topicId).stream().map(VideoRecommendPoolHotTopic::getVideoId).collect(Collectors.toList()));
    }

    @ApiOperation(value = "发现视频列表")
    @GetMapping("/screen")
    public R<PageList<VideoVO>> listScreenVideo(@RequestParam(required = false, defaultValue = "0") long cursor,
                                                @RequestParam int pageSize){
        PageList<VideoRecommendPool> screenVideos = videoRecommendService.listScreenVideo(getPageDomain());
        if (screenVideos.isEmptyList()){
            return R.ok(new PageList<>());
        }
        List<Video> videos = videoService.listVideo(screenVideos.getList().stream().map(VideoRecommendPool::getVideoId).collect(Collectors.toList()));
        if (videos.size() == 0){
            return R.ok(new PageList<>());
        }
        List<VideoVO> videoList = prepare(getCurrentCustomerId(), videos);
        return R.ok(PageList.map(screenVideos, videoList));
    }

    private List<VideoVO> prepare(long currentCustomerId, List<Video> videos){
        Map<Long, VideoStatistic> videoStatisticMap = videoMetricService.listVideoStatisticMap(videos.stream().map(Video::getId).collect(Collectors.toList()));
        Map<Long, SimpleCustomerDTO> customers = customerRpcService.listSimpleCustomer(currentCustomerId, CustomerRelationEnum.FOLLOW.getValue()
                , StringUtil.toString(videos.stream().map(Video::getCustomerId).collect(Collectors.toSet()))).getData();
        Map<String, String> mediaUrls =vodRpcService.listMediaUrl(ListMediaUrlAO.builder().fileIds(videos.stream().map(Video::getFileId).collect(Collectors.toList()))
                .mediaType(MediaTypeEnum.COVER).build());
        List<VideoVO> result = new ArrayList<>(videos.size());
        videos.forEach(item ->{
            VideoVO video = BeanUtil.prepare(item, VideoVO.class);
            video.setCoverUrl(mediaUrls.getOrDefault(item.getFileId(), Strings.EMPTY));
            video.setCustomer(customers.get(item.getCustomerId()));
            video.setStatistic(BeanUtil.prepare(videoStatisticMap.getOrDefault(item.getId(), new VideoStatistic()), VideoStatisticVO.class));
            result.add(video);
        });
        return result;
    }
}
