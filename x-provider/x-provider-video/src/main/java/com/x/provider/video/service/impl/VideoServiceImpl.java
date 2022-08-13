package com.x.provider.video.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.core.utils.JsonUtil;
import com.x.core.web.api.R;
import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.model.dto.IncCustomerStatRequestDTO;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.api.oss.enums.GreenDataTypeEnum;
import com.x.core.domain.SuggestionTypeEnum;
import com.x.provider.api.oss.model.dto.oss.GreenRequestDTO;
import com.x.provider.api.oss.model.dto.vod.ContentReviewResultDTO;
import com.x.provider.api.oss.model.dto.vod.MediaInfoDTO;
import com.x.provider.api.oss.service.GreenRpcService;
import com.x.provider.api.oss.service.VodRpcService;
import com.x.provider.api.video.constants.VideoEventTopic;
import com.x.provider.api.video.enums.VideoStatusEnum;
import com.x.provider.api.video.model.event.VideoChangedEvent;
import com.x.provider.video.constant.Constants;
import com.x.provider.video.enums.VideoErrorEnum;
import com.x.provider.video.enums.VideoTitleItemTypeEnum;
import com.x.provider.video.mapper.VideoAttributeMapper;
import com.x.provider.video.mapper.VideoMapper;
import com.x.provider.video.mapper.VideoTopicMapper;
import com.x.provider.video.model.ao.homepage.CreateVideoAO;
import com.x.provider.video.model.domain.*;
import com.x.provider.video.service.RedisKeyService;
import com.x.provider.video.service.TopicService;
import com.x.provider.video.service.VideoService;
import com.x.redis.domain.LongTypeTuple;
import com.x.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {

    private final String TOPIC_PREFIX = "#";

    private final VideoMapper videoMapper;
    private final VideoTopicMapper videoTopicMapper;
    private final TopicService topicService;
    private final VodRpcService vodRpcService;
    private final Executor executor;
    private final VideoAttributeMapper videoAttributeMapper;
    private final GreenRpcService greenRpcService;
    private final RedisKeyService redisKeyService;
    private final RedisService redisService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CustomerRpcService customerRpcService;

    public VideoServiceImpl(VideoMapper videoMapper,
                            TopicService topicService,
                            VideoTopicMapper videoTopicMapper,
                            VodRpcService vodRpcService,
                            Executor executor,
                            VideoAttributeMapper videoAttributeMapper,
                            GreenRpcService greenRpcService,
                            RedisKeyService redisKeyService,
                            RedisService redisService,
                            KafkaTemplate<String, Object> kafkaTemplate,
                            CustomerRpcService customerRpcService){
        this.videoMapper = videoMapper;
        this.topicService = topicService;
        this.videoTopicMapper = videoTopicMapper;
        this.vodRpcService = vodRpcService;
        this.executor = executor;
        this.videoAttributeMapper = videoAttributeMapper;
        this.greenRpcService = greenRpcService;
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
        this.kafkaTemplate = kafkaTemplate;
        this.customerRpcService = customerRpcService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createVideo(CreateVideoAO createVideoAO, long customerId) {
        R<String> greenResult = greenRpcService.greenSync(GreenRequestDTO.builder().dataType(GreenDataTypeEnum.TEXT.name()).value(createVideoAO.getTitle()).build());
        ApiAssetUtil.isTrue(SuggestionTypeEnum.PASS.name().equals(greenResult.getData()), VideoErrorEnum.VIDEO_TITLE_REVIEW_BLOCKED);
        List<ProductTitleItem> videoTitleList = BeanUtil.prepare(createVideoAO.getProductTitleItemList(), ProductTitleItem.class);
        final List<ProductTitleItem> needCreteVideoTopic = videoTitleList.stream().filter(item -> VideoTitleItemTypeEnum.TOPIC.getValue().equals(item.getVideoTitleType()) &&
                (item.getKey() == null || item.getKey() <= 0 ))
                .collect(Collectors.toList());
        if (!needCreteVideoTopic.isEmpty()){
            final Map<String, Topic> createdTopic = topicService.listOrCreateTopics(new ArrayList<>(needCreteVideoTopic.stream().map(item -> item.getText()).collect(Collectors.toSet()))).stream()
                    .collect(Collectors.toMap(item -> item.getTitle(), item -> item));
            needCreteVideoTopic.stream().forEach(item -> {
                item.setKey(createdTopic.get(item.getKey()).getId());
            });
        }

        Video video = Video.builder().customerId(customerId).fileId(createVideoAO.getFileId()).reviewed(false).title(createVideoAO.getTitle()).videoStatus(VideoStatusEnum.REVIEW.ordinal()).build();
        if (!videoTitleList.isEmpty()){
            video.setTitleItemJson(JsonUtil.toJSONString(videoTitleList));
        }
        videoMapper.insert(video);
        needCreteVideoTopic.stream().filter(item -> VideoTitleItemTypeEnum.TOPIC.getValue().equals(item.getVideoTitleType())).forEach(item -> {
            videoTopicMapper.insert(VideoTopic.builder().topicId(item.getKey()).videoId(video.getId()).videoStatus(video.getVideoStatus()).build());
        });
        //以后这个地方改为kafka
        executor.execute(() -> {
            //vodRpcService.contentReview(GetContentReviewResultAO.builder().fileIds(Arrays.asList(video.getFileId())).notifyUrl(Constants.VIDEO_REVIEW_NOTIFY_RUL).build());
        });
        return video.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideo(long id) {
        Optional<Video> video = getVideo(id, null);
        if (video.isEmpty()){
            return;
        }
        videoMapper.deleteById(id);
        videoTopicMapper.delete(new LambdaQueryWrapper<VideoTopic>().eq(VideoTopic::getVideoId, id));
        sendVideoChangedEvent(video.get(), VideoChangedEvent.EventTypeEnum.VIDEO_DELETED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onVodContentReview(ContentReviewResultDTO contentReviewResultDTO){
        var videoOpt = getVideo(contentReviewResultDTO.getFileId());
        if (videoOpt.isEmpty()){
            log.error("no video find, file id:{}", contentReviewResultDTO.getFileId());
            return;
        }
        VideoStatusEnum videoStatus = SuggestionTypeEnum.BLOCK.name().equals(contentReviewResultDTO.getReviewResult()) ? VideoStatusEnum.UNPUBLISH : VideoStatusEnum.PUBLISH;
        Video video = videoOpt.get();
        video.setReviewed(true);
        var videoTopics = videoTopicMapper.selectList(new LambdaQueryWrapper<VideoTopic>().eq(VideoTopic::getVideoId, video.getId()));
        if (VideoStatusEnum.PUBLISH == videoStatus) {
            video.setVideoStatus(videoStatus.ordinal());
            R<MediaInfoDTO> mediaInfo = vodRpcService.getMediaInfo(video.getFileId());
            if (mediaInfo.getData() == null){
                return;
            }
            video.setDuration(mediaInfo.getData().getDuration());
            videoMapper.updateById(video);
            videoTopics.forEach(item -> {
                item.setVideoStatus(video.getVideoStatus());
                videoTopicMapper.updateById(item);
            });
            sendVideoChangedEvent(video, VideoChangedEvent.EventTypeEnum.VIDEO_PUBLISHED);
        }
        else{
            sendVideoChangedEvent(video, VideoChangedEvent.EventTypeEnum.VIDEO_GREEN_BLOCKED);
            videoMapper.deleteById(video.getId());
            videoTopics.forEach(item -> {
                videoTopicMapper.deleteBatchIds(videoTopics.stream().map(VideoTopic::getId).collect(Collectors.toList()));
            });
            sendVideoChangedEvent(video, VideoChangedEvent.EventTypeEnum.VIDEO_DELETED);
        }
    }

    @Override
    public void topMyVideo(long videoId, boolean top, long customerId) {
        var videoOpt = getVideo(videoId, null);
        ApiAssetUtil.isTrue(videoOpt.isPresent() && videoOpt.get().getCustomerId() == customerId);
        long topValue = top ? System.currentTimeMillis() : 0L;
        videoMapper.updateById(Video.builder().id(videoOpt.get().getId()).top(top).topValue(topValue).build());
        return;
    }

    @Override
    public Optional<Video> getVideo(long videoId) {
        return getVideo(videoId, null);
    }

    @Override
    public PageList<Video> listVideo(long customerId, PageDomain pageDomain){
        LambdaQueryWrapper<Video> query = buildQuery(customerId, VideoStatusEnum.PUBLISH.ordinal()).orderByDesc(Video::getTopValue).orderByDesc(Video::getId)
                .last(StrUtil.format(" limit {} ", pageDomain.getPageSize()));
        if (pageDomain.getCursor() > 0){
            query.lt(Video::getId, pageDomain.getCursor());
        }
        List<Video> videos = videoMapper.selectList(query);
        if (videos.isEmpty()){
            return new PageList<>();
        }
        return new PageList<>(videos, pageDomain.getPageSize(), CollectionUtils.lastElement(videos).getId());
    }

    @Override
    public List<Video> listVideo(List<Long> ids) {
        Map<Long, Video> videoMap = videoMapper.selectList(new LambdaQueryWrapper<Video>().in(Video::getId, ids)).stream().collect(Collectors.toMap(item -> item.getId(), item -> item));
        List<Video> result = new ArrayList<>(ids.size());
        ids.forEach(item ->{
            if (videoMap.containsKey(item)){
                result.add(videoMap.get(item));
            }
        });
        return result;
    }

    @Override
    public void onStar(StarEvent starEvent) {
        if (starEvent.getItemType().equals(StarItemTypeEnum.VIDEO.getValue())){
            final Optional<Video> video = getVideo(Long.parseLong(starEvent.getItemId()));
            if(!video.isPresent()){
              return;
            }
            customerRpcService.incCustomerStatAO(IncCustomerStatRequestDTO.builder().starCount(starEvent.isStar() ? 1L : -1L).id(video.get().getCustomerId()).build());
            if(starEvent.isStar()) {
                addVideoStarList(Long.parseLong(starEvent.getItemId()), starEvent.getStarCustomerId());
                return;
            }
            removeFromVideoStarList(Long.parseLong(starEvent.getItemId()), starEvent.getStarCustomerId());
        }
    }

    @Override
    public PageList<Video> listCustomerStarVideo(PageDomain pageDomain, long starCustomerId) {
        Set<LongTypeTuple> videoWithScore = redisService.reverseRangeByScoreLong(redisKeyService.getCustomerStarVideoKey(starCustomerId), pageDomain.getCursor(), pageDomain.getPageSize());
        if (videoWithScore.isEmpty()){
            return new PageList<>();
        }
        List<Long> starVideoIds = videoWithScore.stream().map(ZSetOperations.TypedTuple::getValue).collect(Collectors.toList());
        List<Video> videos = listVideo(starVideoIds);
        if (starVideoIds.size() != videos.size()){
            starVideoIds.removeAll(videos.stream().map(Video::getId).collect(Collectors.toSet()));
            starVideoIds.parallelStream().forEach(item -> {
                removeFromVideoStarList(item, starCustomerId);
            });
            return listCustomerStarVideo(pageDomain, starCustomerId);
        }
        List<Video> result = new ArrayList<>(videos.size());
        Map<Long, Video> videoMap = videos.stream().collect(Collectors.toMap(Video::getId, item -> item));
        videoWithScore.forEach(item -> {
            result.add(videoMap.get(item.getValue()));
        });
        return new PageList<>(result, pageDomain.getPageSize(), CollectionUtils.lastElement(videoWithScore).getScore().longValue());
    }

    @Override
    public List<VideoTopic> listVideoTopic(Long videoId){
        LambdaQueryWrapper<VideoTopic> query = new LambdaQueryWrapper<>();
        if (videoId != null){
            query = query.eq(VideoTopic::getVideoId, videoId);
        }
        return videoTopicMapper.selectList(query);
    }

    private List<Video> listVideo(Set<Long> ids){
        return videoMapper.selectBatchIds(ids);
    }

    private void addVideoStarList(Long videoId, Long customerId){
        redisService.zadd(redisKeyService.getCustomerStarVideoKey(customerId), videoId, System.currentTimeMillis());
    }

    private void removeFromVideoStarList(Long videoId, Long customerId){
        redisService.zremove(redisKeyService.getCustomerStarVideoKey(customerId), videoId);
    }

    public Optional<Video> getVideo(String fileId){
        return getVideo(0, fileId);
    }

    private Optional<VideoAttribute> getVideoAttribute(long videoId){
        return listVideoAttribute(videoId).stream().findFirst();
    }

    private List<VideoAttribute> listVideoAttribute(long videoId){
        var query = new LambdaQueryWrapper<VideoAttribute>();
        if (videoId > 0){
            query.eq(VideoAttribute::getVideoId, videoId);
        }
        return videoAttributeMapper.selectList(query);
    }
    private Optional<Video> getVideo(long id, String fileId){
        var query = new LambdaQueryWrapper<Video>();
        if (!StringUtils.isEmpty(fileId)){
            query.eq(Video::getFileId, fileId);
        }
        if (id > 0){
            query.eq(Video::getId, id);
        }
        query = query.orderByDesc(Video::getId);
        return Optional.ofNullable(videoMapper.selectOne(query.last(" limit 1 ")));
    }

    private List<String> parseVideoTopicTitle(String text){
        String[] splitText = text.split(" ");
        List<String> result = new ArrayList<>(splitText.length);
        Arrays.stream(splitText).filter(item -> item.startsWith(TOPIC_PREFIX)).forEach(item ->{
            result.add(item.replaceFirst(TOPIC_PREFIX, ""));
        });
        return result;
    }

    @Override
    public List<Video> listVideo(List<Long> customerIds, Date afterUpdateDate){
        return videoMapper.selectList(new LambdaQueryWrapper<Video>().in(Video::getCustomerId, customerIds).eq(Video::getVideoStatus, VideoStatusEnum.PUBLISH.ordinal()).ge(Video::getUpdatedOnUtc, afterUpdateDate));
    }

    private void sendVideoChangedEvent(Video video, VideoChangedEvent.EventTypeEnum eventTypeEnum){
        VideoChangedEvent changedEvent = BeanUtil.prepare(video, VideoChangedEvent.class);
        changedEvent.setEventType(eventTypeEnum.getValue());
        kafkaTemplate.send(VideoEventTopic.TOPIC_NAME_VIDEO_CHANGED, String.valueOf(video.getId()), changedEvent);
    }

    private LambdaQueryWrapper<Video> buildQuery(long customerId, Integer videoStatus){
        LambdaQueryWrapper<Video> query = new LambdaQueryWrapper<>();
        if (customerId > 0){
            query.eq(Video::getCustomerId, customerId);
        }
        if (videoStatus != null){
            query.eq(Video::getVideoStatus, videoStatus);
        }
        return query;
    }

    private List<ProductTitleItem> parse(String title){
        if (StringUtils.isEmpty(title)){
            return new ArrayList<>();
        }
        final String[] split = title.split(Constants.PRODUCT_TITLE_SPLITTER);
        List<ProductTitleItem> result = new ArrayList<>();
        Arrays.stream(split).forEach(item -> {
            ProductTitleItem videoTitle = new ProductTitleItem();
            if (item.startsWith(Constants.PRODUCT_TITLE_TOPIC_PREFIX)){
                videoTitle.setText(item.replaceFirst(Constants.PRODUCT_TITLE_TOPIC_PREFIX, Constants.PRODUCT_TITLE_TOPIC_PREFIX));
                videoTitle.setVideoTitleType(VideoTitleItemTypeEnum.TOPIC.getValue());
            }
            else{
                videoTitle.setText(item);
                videoTitle.setVideoTitleType(VideoTitleItemTypeEnum.TEXT.getValue());
            }
            result.add(videoTitle);
        });
        return result;
    }
}
