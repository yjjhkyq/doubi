package com.x.provider.video.controller.frontent;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.exception.ApiException;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.core.web.page.*;
import com.x.provider.api.customer.enums.CustomerOptions;
import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.api.customer.model.dto.CustomerDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.general.enums.CommentItemTypeEnum;
import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.general.model.ao.CommentAO;
import com.x.provider.api.general.model.ao.CommentReplyAO;
import com.x.provider.api.general.model.ao.ListCommentAO;
import com.x.provider.api.general.model.ao.StarAO;
import com.x.provider.api.general.model.dto.CommentDTO;
import com.x.provider.api.general.service.CommentRpcService;
import com.x.provider.api.general.service.StarRpcService;
import com.x.provider.api.oss.service.OssRpcService;
import com.x.provider.api.statistic.enums.StatTotalItemNameEnum;
import com.x.provider.api.statistic.enums.StatisticObjectClassEnum;
import com.x.provider.api.statistic.enums.StatisticPeriodEnum;
import com.x.provider.api.statistic.model.ao.ListStatTotalAO;
import com.x.provider.api.statistic.model.ao.ListStatisticTotalBatchAO;
import com.x.provider.api.statistic.model.dto.ListStatisticTotalDTO;
import com.x.provider.api.statistic.service.StatisticTotalRpcService;
import com.x.provider.api.video.enums.VideoStatusEnum;
import com.x.provider.api.vod.enums.MediaTypeEnum;
import com.x.provider.api.vod.model.ao.ListMediaUrlAO;
import com.x.provider.api.vod.service.VodRpcService;
import com.x.provider.video.enums.VideoErrorEnum;
import com.x.provider.video.model.ao.*;
import com.x.provider.video.model.ao.homepage.CreateVideoAO;
import com.x.provider.video.model.ao.homepage.TopMyVideoAO;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoRecommendPool;
import com.x.provider.video.model.domain.VideoRecommendPoolHotTopic;
import com.x.provider.video.model.vo.CommentVO;
import com.x.provider.video.model.vo.StatisticVO;
import com.x.provider.video.model.vo.VideoDetailVO;
import com.x.provider.video.model.vo.homepage.VideoItemList;
import com.x.provider.video.service.VideoReadService;
import com.x.provider.video.service.VideoMetricService;
import com.x.provider.video.service.VideoService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
    private final Executor executor;
    private final OssRpcService ossRpcService;
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
        this.executor = executor;
        this.ossRpcService = ossRpcService;
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
    public R<VideoDetailVO> detail(@RequestParam  long id) throws ExecutionException, InterruptedException {
        Video video = videoService.getVideo(id).orElseThrow(() -> new ApiException(VideoErrorEnum.VIDEO_NOT_EXISTED));
        long currentCustomerId =  getCurrentCustomerId();
        CompletableFuture<R<CustomerDTO>> customerFuture = CompletableFuture.supplyAsync(() -> customerRpcService.getCustomer(video.getCustomerId(),
                Arrays.asList(CustomerOptions.CUSTOMER_ATTRIBUTE.name())), executor);
        CompletableFuture<R<Integer>> customerRelationFuture = CompletableFuture.supplyAsync(() -> 0 == currentCustomerId ? R.ok(CustomerRelationEnum.NO_RELATION.getValue()) :
                customerRpcService.getCustomerRelation(currentCustomerId, video.getCustomerId()));
        ListStatisticTotalBatchAO listStatisticTotalBatchAO = new ListStatisticTotalBatchAO();
        listStatisticTotalBatchAO.setConditions(Arrays.asList(
                ListStatTotalAO.builder().statTotalItemNameEnum(StatTotalItemNameEnum.VIDEO_STAR_COUNT.getValue())
                .statisticPeriod(StatisticPeriodEnum.ALL.getValue()).statisticObjectIds(Arrays.asList(video.getId().toString())).statisticObjectClassEnum(StatisticObjectClassEnum.VIDEO.getValue())
                .build(),
                ListStatTotalAO.builder().statTotalItemNameEnum(StatTotalItemNameEnum.VIDEO_COMMENT_COUNT.getValue())
                        .statisticPeriod(StatisticPeriodEnum.ALL.getValue()).statisticObjectIds(Arrays.asList(video.getId().toString())).statisticObjectClassEnum(StatisticObjectClassEnum.VIDEO.getValue())
                        .build(),
                ListStatTotalAO.builder().statTotalItemNameEnum(StatTotalItemNameEnum.VIDEO_PLAY_COUNT.getValue())
                        .statisticPeriod(StatisticPeriodEnum.ALL.getValue()).statisticObjectIds(Arrays.asList(video.getId().toString())).statisticObjectClassEnum(StatisticObjectClassEnum.VIDEO.getValue())
                        .build()
        ));
        CompletableFuture<R<List<ListStatisticTotalDTO>>> statFuture = CompletableFuture.supplyAsync(() -> statisticTotalRpcService.listStatisticTotalBatch(listStatisticTotalBatchAO));
        CompletableFuture.allOf(customerFuture, customerRelationFuture, statFuture).join();
        R<String> avatarUrl = ossRpcService.getObjectBrowseUrl(customerFuture.get().getData().getCustomerAttribute().getAvatarId());
        return R.ok(VideoDetailVO.builder().id(video.getId()).title(video.getTitle()).top(video.getTop()).videoStatus(video.getVideoStatus())
                .toAuthorRelation(customerRelationFuture.get().getData()).authorId(video.getCustomerId()).authorAvatarUrl(avatarUrl.getData())
                .authorNickName(customerFuture.get().getData().getCustomerAttribute().getNickName()).fileId(video.getFileId())
                .myVideo(!video.getCustomerId().equals(getCurrentCustomerId()) && CustomerRelationEnum.NO_RELATION.getValue() == customerRelationFuture.get().getData())
                .statistic(prepare(statFuture.get().getData())).createdOnUtc(video.getCreatedOnUtc())
                .build());
    }

    @ApiOperation(value = "我的视频")
    @GetMapping("/homepage/list")
    public R<TableDataInfo<VideoItemList>> list(@RequestParam @Min(1) long customerId){
        IPage<Video> videos = videoService.listVideo(customerId, TableSupport.buildIPageRequest());
        if (videos.getSize() == 0){
            return R.ok(new TableDataInfo());
        }
        boolean starCount = customerId != getCurrentCustomerId();
        List<VideoItemList> videoItemLists = prepare(videos.getRecords(), starCount, !starCount);
        return R.ok(TableSupport.buildTableDataInfo(TableSupport.getPageDomain(), videoItemLists));
    }

    @ApiOperation(value = "点赞/取消点赞视频")
    @PostMapping("/star")
    public R<Void> starVideo(@RequestBody @Validated StarVideoAO starVideoAO){
        videoMetricService.star(getCurrentCustomerId(), starVideoAO.getVideoId(), starVideoAO.isStar());
        return R.ok();
    }

    @ApiOperation(value = "点赞视频列表")
    @GetMapping("/star/list")
    public R<TableDataInfo<VideoItemList>> customerStarVideoList(@RequestParam @Min(1) long customerId){
        List<Video> videos = videoService.listCustomerStarVideo(TableSupport.getPageDomain(), getCurrentCustomerId());
        if (videos.size() == 0){
            return R.ok(new TableDataInfo());
        }
        List<VideoItemList> videoItemLists = prepare(videos, true, false);
        return R.ok(TableSupport.buildTableDataInfo(TableSupport.getPageDomain(), videoItemLists));
    }

    @ApiOperation(value = "上报视频实际播放时长")
    @PostMapping("/metric/play/duration/report")
    public R<Void> reportVideoPlayMetric(@RequestBody @Validated ReportVideoPlayMetricAO reportVideoPlayMetricAO){
        videoMetricService.reportVideoPlayMetric(reportVideoPlayMetricAO.getVideoId(), getCurrentCustomerIdAndNotCheckLogin(), reportVideoPlayMetricAO.getPlayDuration());
        return R.ok();
    }

    @ApiOperation(value = "查询视频评论")
    @GetMapping("/comment/list")
    public R<TableDataInfo<CommentVO>> listVideoComment(@RequestParam long videoId){
        Optional<Video> video = videoService.getVideo(videoId);
        ApiAssetUtil.isTrue(video.isPresent(), VideoErrorEnum.VIDEO_NOT_EXISTED);
        TableDataInfo<CommentDTO> comments = commentRpcService.listComment(ListCommentAO.builder().itemId(videoId)
                .itemType(CommentItemTypeEnum.VIDEO.getValue()).pageDomain(TableSupport.getPageDomain()).build()).getData();
        if (comments.getList().size() == 0){
            return R.ok(new TableDataInfo<>());
        }
        PageDomain authorReplyPage = new PageDomain();
        authorReplyPage.setPageNum(0);
        authorReplyPage.setPageSize(Integer.MAX_VALUE);
        List<CommentDTO> authorReplyComment = commentRpcService.listComment(ListCommentAO.builder().itemId(videoId).itemType(CommentItemTypeEnum.VIDEO.getValue()).commentCustomerId(video.get().getCustomerId())
                .pageDomain(authorReplyPage).build()).getData().getList().stream().filter(item -> item.getReplyCommentId() > 0).collect(Collectors.toList());
        Map<Long, ArrayList<CommentVO>> authorReplyList = new HashMap<>(authorReplyComment.size());
        if (!authorReplyComment.isEmpty()) {
            Map<Long, Long> commentStarCountMap = statisticTotalRpcService.listStatisticTotal(ListStatTotalAO.builder().statTotalItemNameEnum(StatTotalItemNameEnum.VIDEO_COMMENT_STAR_COUNT.getValue())
                    .statisticPeriod(StatisticPeriodEnum.ALL.getValue()).statisticObjectClassEnum(StatisticObjectClassEnum.COMMENT.getValue())
                    .statisticObjectIds(authorReplyComment.stream().map(CommentDTO::getId).map(String::valueOf).collect(Collectors.toList())).build()).getData().stream()
                    .collect(Collectors.toMap(item -> Long.parseLong(item.getStatisticObjectId()), item -> item.getLongValue()));
            authorReplyComment.forEach(item -> {
                if (!authorReplyList.containsKey(item.getReplyCommentId())) {
                    authorReplyList.put(item.getReplyCommentId(), new ArrayList<>());
                }
                ArrayList<CommentVO> commentList = authorReplyList.get(item.getReplyCommentId());
                CommentVO comment = BeanUtil.prepare(item, CommentVO.class);
                comment.setAuthor(true);
                comment.setStarCount(commentStarCountMap.getOrDefault(item.getId(), 0L));
                commentList.add(comment);
            });
        }
        Map<Long, Long> commentReplyCountMap = statisticTotalRpcService.listStatisticTotal(ListStatTotalAO.builder().statTotalItemNameEnum(StatTotalItemNameEnum.VIDEO_COMMENT_REPLY_COUNT.getValue())
                .statisticPeriod(StatisticPeriodEnum.ALL.getValue()).statisticObjectClassEnum(StatisticObjectClassEnum.COMMENT.getValue())
                .statisticObjectIds(comments.getList().stream().map(CommentDTO::getId).map(String::valueOf).collect(Collectors.toList())).build()).getData().stream()
                .collect(Collectors.toMap(item -> Long.parseLong(item.getStatisticObjectId()), item -> item.getLongValue()));
        Map<Long, Long> commentStarCountMap = statisticTotalRpcService.listStatisticTotal(ListStatTotalAO.builder().statTotalItemNameEnum(StatTotalItemNameEnum.VIDEO_COMMENT_STAR_COUNT.getValue())
                .statisticPeriod(StatisticPeriodEnum.ALL.getValue()).statisticObjectClassEnum(StatisticObjectClassEnum.COMMENT.getValue())
                .statisticObjectIds(comments.getList().stream().map(CommentDTO::getId).map(String::valueOf).collect(Collectors.toList())).build()).getData().stream()
                .collect(Collectors.toMap(item -> Long.parseLong(item.getStatisticObjectId()), item -> item.getLongValue()));
        TableDataInfo<CommentVO> result = comments.prepare(item -> {
            CommentVO commentVO = BeanUtil.prepare(item, CommentVO.class);
            commentVO.setAuthorReplyList(authorReplyList.getOrDefault(item.getId(), new ArrayList<>()));
            commentVO.setReplyNeedShowTotalCount(Math.max(0, commentReplyCountMap.getOrDefault(item.getId(), 0L) - commentVO.getAuthorReplyList().size()));
            commentVO.setStarCount(commentStarCountMap.getOrDefault(item.getId(), 0L));
            return commentVO;
        });
        return R.ok(result);
    }

    @ApiOperation(value = "查询视频评论回复列表")
    @GetMapping("/comment/reply/list")
    public R<TableDataInfo<CommentVO>> listVideoCommentReply(@ApiParam("视频id") @RequestParam long videoId, @ApiParam("评论id") @RequestParam long commentId){
        Optional<Video> video = videoService.getVideo(videoId);
        ApiAssetUtil.isTrue(video.isPresent(), VideoErrorEnum.VIDEO_NOT_EXISTED);
        TableDataInfo<CommentDTO> comments = commentRpcService.listComment(ListCommentAO.builder().itemId(videoId)
                .itemType(CommentItemTypeEnum.VIDEO.getValue()).replyRootId(commentId).pageDomain(TableSupport.getPageDomain()).build()).getData();
        if (comments.getList().size() == 0){
            return R.ok(new TableDataInfo<>());
        }
        Map<Long, Long> commentStarCountMap = statisticTotalRpcService.listStatisticTotal(ListStatTotalAO.builder().statTotalItemNameEnum(StatTotalItemNameEnum.VIDEO_COMMENT_STAR_COUNT.getValue())
                .statisticPeriod(StatisticPeriodEnum.ALL.getValue()).statisticObjectClassEnum(StatisticObjectClassEnum.COMMENT.getValue())
                .statisticObjectIds(comments.getList().stream().map(CommentDTO::getId).map(String::valueOf).collect(Collectors.toList())).build()).getData().stream()
                .collect(Collectors.toMap(item -> Long.parseLong(item.getStatisticObjectId()), item -> item.getLongValue()));
        TableDataInfo<CommentVO> result = comments.prepare(item -> {
            CommentVO commentVO = BeanUtil.prepare(item, CommentVO.class);
            commentVO.setAuthor(video.get().getCustomerId().equals(item.getCommentCustomerId()));
            commentVO.setStarCount(commentStarCountMap.getOrDefault(item.getId(), 0L));
            return commentVO;
        });
        return R.ok(result);
    }

    @ApiOperation(value = "评论视频")
    @PostMapping("/comment")
    public R<Void> videoComment(@RequestBody @Validated VideoCommentAO videoCommentAO){
        Optional<Video> video = videoService.getVideo(videoCommentAO.getItemId());
        ApiAssetUtil.isTrue(video.isPresent(), VideoErrorEnum.VIDEO_NOT_EXISTED);
        if(!video.get().getVideoStatus().equals(VideoStatusEnum.PUBLISH.ordinal())){
            return R.ok();
        }
        return commentRpcService.comment(CommentAO.builder().commentCustomerId(getCurrentCustomerId()).content(videoCommentAO.getContent()).itemType(CommentItemTypeEnum.VIDEO.getValue())
                .itemId(videoCommentAO.getItemId()).build());
    }

    @ApiOperation(value = "回复视频评论")
    @PostMapping("/comment/reply")
    public R<Void> videoCommentReply(@RequestBody @Validated VideoCommentReplyAO videoCommentReplyAO){
        return commentRpcService.commentReply(CommentReplyAO.builder().commentCustomerId(getCurrentCustomerId()).commentId(videoCommentReplyAO.getCommentId())
                .content(videoCommentReplyAO.getContent()).build());
    }

    @ApiOperation(value = "点赞/取消点赞评论")
    @PostMapping("/comment/star")
    public R<Void> videoCommentStar(@RequestBody @Validated VideoCommentStarAO videoCommentStarAO){
        Optional<Video> video = videoService.getVideo(videoCommentStarAO.getVideoId());
        ApiAssetUtil.isTrue(video.isPresent(), VideoErrorEnum.VIDEO_NOT_EXISTED);
        if(!video.get().getVideoStatus().equals(VideoStatusEnum.PUBLISH.ordinal())){
            return R.ok();
        }
        return starRpcService.star(StarAO.builder().associationItemId(videoCommentStarAO.getVideoId()).itemId(videoCommentStarAO.getCommentId()).itemType(StarItemTypeEnum.COMMENT.getValue())
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
    public R<CursorList<VideoItemList>> listScreenVideo(@RequestBody @Validated CursorPageRequest cursorPageRequest){
        CursorList<Long> screenVideos = videoRecommendService.listScreenVideo(cursorPageRequest);
        if (screenVideos.getList().size() == 0){
            return R.ok(new CursorList<>());
        }
        List<Video> videos = videoService.listVideo(screenVideos.getList());
        if (videos.size() == 0){
            return R.ok(new CursorList<>());
        }
        List<VideoItemList> videoItems = prepare(videos, true, false);
        return R.ok(screenVideos.prepare(videoItems));
    }

    private List<VideoItemList> prepare(List<Video> videos,  boolean starCount, boolean playCount){
        if (videos.size() == 0){
            return Collections.emptyList();
        }
        var mediaUrls =vodRpcService.listMediaUrl(ListMediaUrlAO.builder().fileIds(videos.stream().map(Video::getFileId).collect(Collectors.toList()))
                .mediaType(MediaTypeEnum.COVER).build());
        final Map<String, Long> videoStarCount = new HashMap<>();
        final Map<String, Long> playCountMap = new HashMap<>();
        List<String> ids = videos.stream().map(item -> String.valueOf(item.getId())).collect(Collectors.toList());
        if (starCount) {
            videoStarCount.putAll(statisticTotalRpcService.listStatisticTotal(ListStatTotalAO.builder().statisticObjectClassEnum(StatisticObjectClassEnum.VIDEO.getValue()).statisticObjectIds(ids)
                    .statisticPeriod(StatisticPeriodEnum.ALL.getValue()).statTotalItemNameEnum(StatTotalItemNameEnum.VIDEO_STAR_COUNT.getValue()).build()).getData().stream()
                    .collect(Collectors.toMap(item -> item.getStatisticObjectId(), item -> item.getLongValue())));
        }
        if (playCount){
            playCountMap.putAll(statisticTotalRpcService.listStatisticTotal(ListStatTotalAO.builder().statisticObjectClassEnum(StatisticObjectClassEnum.VIDEO.getValue()).statisticObjectIds(ids)
                    .statisticPeriod(StatisticPeriodEnum.ALL.getValue()).statTotalItemNameEnum(StatTotalItemNameEnum.VIDEO_PLAY_COUNT.getValue()).build()).getData().stream()
                    .collect(Collectors.toMap(item -> item.getStatisticObjectId(), item -> item.getLongValue())));
        }
        List<VideoItemList> result = new ArrayList<>(videos.size());
        videos.forEach(item -> {
            result.add( VideoItemList.builder().coverUrl(mediaUrls.get(item.getFileId())).id(item.getId()).top(item.getTop())
                    .videoStatus(item.getVideoStatus()).customerId(item.getCustomerId()).statistic(StatisticVO.builder()
                            .playCount(playCountMap.getOrDefault(String.valueOf(item.getId()), 0L))
                            .starCount(videoStarCount.getOrDefault(String.valueOf(item.getId()), 0L)).build()).build());
        });
        return result;
    }

    private StatisticVO prepare(List<ListStatisticTotalDTO> listStatisticTotalDTOS){
        Map<Integer, Long> stats = listStatisticTotalDTOS.stream().collect(Collectors.toMap(item -> item.getStatTotalItemNameEnum(), item -> item.getLongValue()));
        return StatisticVO.builder().playCount(stats.getOrDefault(StatTotalItemNameEnum.VIDEO_PLAY_COUNT.getValue(), 0L))
                .commentCount(stats.getOrDefault(StatTotalItemNameEnum.VIDEO_COMMENT_COUNT.getValue(), 0L))
                .starCount(stats.getOrDefault(StatTotalItemNameEnum.VIDEO_STAR_COUNT.getValue(), 0L)).build();
    }
}
