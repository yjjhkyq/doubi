package com.x.provider.video.controller.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.x.core.exception.ApiException;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.core.utils.JsonUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.core.web.page.PageHelper;
import com.x.core.web.page.PageList;
import com.x.provider.api.common.enums.ItemTypeEnum;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.model.dto.ListSimpleCustomerRequestDTO;
import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.general.enums.CommentItemTypeEnum;
import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.general.model.dto.CommentRequestDTO;
import com.x.provider.api.general.model.dto.ListStarRequestDTO;
import com.x.provider.api.general.model.dto.StarRequestDTO;
import com.x.provider.api.general.service.CommentRpcService;
import com.x.provider.api.general.service.StarRpcService;
import com.x.provider.api.oss.model.dto.vod.ListMediaRequestDTO;
import com.x.provider.api.oss.model.dto.vod.MediaInfoDTO;
import com.x.provider.api.oss.service.VodRpcService;
import com.x.provider.api.video.enums.VideoStatusEnum;
import com.x.provider.video.enums.VideoErrorEnum;
import com.x.provider.video.model.ao.ReportVideoPlayMetricAO;
import com.x.provider.video.model.ao.StarVideoAO;
import com.x.provider.video.model.ao.VideoCommentAO;
import com.x.provider.video.model.ao.VideoCommentStarAO;
import com.x.provider.video.model.ao.homepage.CreateVideoAO;
import com.x.provider.video.model.ao.homepage.TopMyVideoAO;
import com.x.provider.video.model.domain.*;
import com.x.provider.video.model.vo.ProductTitleItemVO;
import com.x.provider.video.model.vo.VideoStatisticVO;
import com.x.provider.video.model.vo.VideoVO;
import com.x.provider.video.service.VideoMetricService;
import com.x.provider.video.service.VideoReadService;
import com.x.provider.video.service.VideoService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@ApiModel(value = "视频服务")
@RestController
@RequestMapping("/app/video")
public class VideoController extends BaseFrontendController {

    private final VideoService videoService;
    private final VodRpcService vodRpcService;
    private final CustomerRpcService customerRpcService;
    private final VideoMetricService videoMetricService;
    private final CommentRpcService commentRpcService;
    private final StarRpcService starRpcService;
    private final VideoReadService videoRecommendService;

    public VideoController(VideoService videoService,
                           VodRpcService vodRpcService,
                           CustomerRpcService customerRpcService,
                           @Qualifier("videoDefaultExecutor") Executor executor,
                           VideoMetricService videoMetricService,
                           CommentRpcService commentRpcService,
                           StarRpcService starRpcService,
                           VideoReadService videoRecommendService){
        this.videoService = videoService;
        this.vodRpcService = vodRpcService;
        this.customerRpcService = customerRpcService;
        this.videoMetricService = videoMetricService;
        this.commentRpcService = commentRpcService;
        this.starRpcService = starRpcService;
        this.videoRecommendService = videoRecommendService;
    }

    @ApiOperation(value = "创建视频")
    @PostMapping("create")
    public R<Long> createVideo(@RequestBody @Validated CreateVideoAO createVideoAO){
        var videoId = videoService.createVideo(createVideoAO, getCurrentCustomerId());
        return R.ok(videoId);
    }

    @ApiOperation(value = "删除视频")
    @PostMapping("delete")
    public R<Void> deleteVideo(long id){
        videoService.deleteVideo(id);
        return R.ok();
    }

    @ApiOperation(value = "置顶视频")
    @PostMapping("top")
    public R<Void> topMyVideo(@RequestBody @Validated TopMyVideoAO topMyVideoAO){
        videoService.topMyVideo(topMyVideoAO.getVideoId(), topMyVideoAO.isTop(), getCurrentCustomerId());
        return R.ok();
    }

    @ApiOperation(value = "视频详情")
    @GetMapping("detail")
    public R<VideoVO> detail(@RequestParam  long id) throws InterruptedException {
        Video video = videoService.getVideo(id).orElseThrow(() -> new ApiException(VideoErrorEnum.VIDEO_NOT_EXISTED));
        long currentCustomerId =  getCurrentCustomerId();
        return R.ok(prepare(currentCustomerId, Arrays.asList(video)).stream().findFirst().get());
    }

    @ApiOperation(value = "我的视频")
    @GetMapping("/list/customer")
    public R<PageList<VideoVO>> list(@RequestParam(required = false, defaultValue = "0") long cursor,
                                         @RequestParam int pageSize,
                                         @RequestParam @Min(1) long customerId){
        PageList<Video> videos = videoService.listVideo(customerId, PageHelper.getPageDomain());
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
        videoMetricService.reportVideoPlayMetric(reportVideoPlayMetricAO.getVideoId(), getCurrentCustomerId(), reportVideoPlayMetricAO.getPlayDuration());
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
        return commentRpcService.comment(CommentRequestDTO.builder()
                .itemCustomerId(video.get().getCustomerId())
                .parentCommentId(videoCommentAO.getParentCommentId())
                .commentCustomerId(getCurrentCustomerId())
                .content(videoCommentAO.getContent())
                .itemType(CommentItemTypeEnum.VIDEO.getValue())
                .itemId(videoCommentAO.getItemId())
                .commentCustomerId(getCurrentCustomerId())
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
        return starRpcService.star(StarRequestDTO.builder().itemId(videoCommentStarAO.getCommentId()).itemType(StarItemTypeEnum.COMMENT.getValue())
                .star(videoCommentStarAO.isStar()).starCustomerId(getCurrentCustomerId()).build());
    }

    @ApiOperation(value = "我关注的人发布的视频,返回值为视频id列表")
    @GetMapping("/my/follow/person")
    public R<PageList<VideoVO>> listMyFollowPersonVideo(@RequestParam(required = false, defaultValue = "0") long cursor,
                                                        @RequestParam int pageSize){
        long currentCustomerId = getCurrentCustomerId();
        PageList<Video> followPersonVideo = videoRecommendService.listMyFollowPersonVideo(getPageDomain(), currentCustomerId);

        return R.ok(followPersonVideo.map(prepare(currentCustomerId, followPersonVideo.getList())));
    }

    @ApiOperation(value = "我的自选主题下的热门视频")
    @GetMapping("/hot/my/follow/topic")
    public R<PageList<VideoVO>> listMyFollowTopicHotVideo(@RequestParam(required = false, defaultValue = "0") long cursor,
                                                   @RequestParam int pageSize){
        long currentCustomerId = getCurrentCustomerId();
        PageList<Video> videoPageList = videoRecommendService.listMyFollowTopicHotVideo(getPageDomain(), currentCustomerId);
        return R.ok(videoPageList.map(prepare(currentCustomerId, videoPageList.getList())));
    }

    @ApiOperation(value = "热门视频")
    @GetMapping("/hot")
    public R<PageList<VideoVO>> listHotVideo(@RequestParam(required = false, defaultValue = "0") long cursor,
                                      @RequestParam int pageSize){
        PageList<Video> hotVideoList = videoRecommendService.listHotVideo(getPageDomain());
        return R.ok(hotVideoList.map(prepare(getCurrentCustomerIdAndNotCheckLogin(), hotVideoList.getList())));
    }

    @ApiOperation(value = "主题下的热门视频")
    @GetMapping("/hot/topic")
    public R<PageList<VideoVO>> listHotVideoTopic(@RequestParam(required = false, defaultValue = "0") long cursor,
                                           @RequestParam int pageSize,
                                           @ApiParam("主题id") @RequestParam Long topicId){
        PageList<Video> videoPageList = videoRecommendService.listHotVideoTopic(getPageDomain(), topicId);
        return R.ok(videoPageList.map(prepare(getCurrentCustomerIdAndNotCheckLogin(), videoPageList.getList())));
    }

    @ApiOperation(value = "发现视频列表")
    @GetMapping("/screen")
    public R<PageList<VideoVO>> listScreenVideo(@RequestParam(required = false, defaultValue = "0") long cursor,
                                                @RequestParam int pageSize){
        PageList<Video> screenVideos = videoRecommendService.listScreenVideo(getPageDomain());
        return R.ok(screenVideos.map(prepare(getCurrentCustomerIdAndNotCheckLogin(), screenVideos.getList())));
    }

    private List<VideoVO> prepare(long currentCustomerId, List<Video> videos){
        if (videos.isEmpty()){
            return new ArrayList<>();
        }
        List<Long> videoIdList = videos.stream().map(Video::getId).collect(Collectors.toList());
        Map<Long, VideoStatistic> videoStatisticMap = videoMetricService.listVideoStatisticMap(videoIdList);
        Map<Long, SimpleCustomerDTO> customers = customerRpcService.listSimpleCustomer(ListSimpleCustomerRequestDTO.builder()
                        .sessionCustomerId(currentCustomerId)
                        .customerOptions(List.of(CustomerOptions.CUSTOMER_RELATION.name(), CustomerOptions.CUSTOMER_ATTRIBUTE.name()))
                        .customerIds(new ArrayList<>(videos.stream().map(Video::getCustomerId).collect(Collectors.toSet())))
                        .build()).getData();
        Map<String, MediaInfoDTO> mediaInfoMap = vodRpcService.listMediaInfo(ListMediaRequestDTO.builder().fileIdList(videos.stream().map(Video::getFileId).collect(Collectors.toList())).build())
                .getData().stream().collect(Collectors.toMap(item -> item.getFileId(), item -> item));
        List<VideoVO> result = new ArrayList<>(videos.size());
        videos.forEach(item ->{
            VideoVO video = BeanUtil.prepare(item, VideoVO.class);
            video.setCustomer(customers.get(item.getCustomerId()));
            video.setStatistic(BeanUtil.prepare(videoStatisticMap.getOrDefault(item.getId(), new VideoStatistic()), VideoStatisticVO.class));
            MediaInfoDTO mediaInfoDTO = mediaInfoMap.get(item.getFileId());
            if (mediaInfoDTO != null){
                video.setCoverUrl(mediaInfoDTO.getCoverUrl());
                video.setVodUrl(mediaInfoDTO.getVodUrl());
            }
            if (!StringUtils.isEmpty(item.getTitleItemJson())){
                List<ProductTitleItem> productTitleItems = JsonUtil.parseObject(item.getTitleItemJson(), new TypeReference<>() {});
                video.setProductTitleItemList(BeanUtil.prepare(productTitleItems, ProductTitleItemVO.class));
            }
            result.add(video);
        });
        prepareInteract(currentCustomerId, result);
        return result;
    }

    private void prepareInteract(long currentCustomerId, List<VideoVO> dest){
        if (currentCustomerId <= 0){
            return;
        }
        List<Long> videoIdList = dest.stream().map(VideoVO::getId).collect(Collectors.toList());
        Set<Long> starIdSet = starRpcService.listStar(ListStarRequestDTO.builder().itemType(ItemTypeEnum.VIDEO.getValue()).starCustomerId(currentCustomerId).itemIdList(videoIdList).build()).getData()
                .stream().filter(item -> item.isStar()).map(item -> item.getId()).collect(Collectors.toSet());
        if (starIdSet.isEmpty()){
            return;
        }
        dest.stream().filter(item -> starIdSet.contains(item.getId())).forEach(item -> {
            item.getInteract().setStared(true);
        });
    }
}
