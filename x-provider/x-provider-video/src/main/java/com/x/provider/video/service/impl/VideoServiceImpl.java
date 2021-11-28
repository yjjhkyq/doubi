package com.x.provider.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.utils.ApiAssetUtil;
import com.x.core.utils.BeanUtil;
import com.x.core.web.api.R;
import com.x.core.web.page.PageDomain;
import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.api.general.model.event.StarRequestEvent;
import com.x.provider.api.oss.enums.GreenDataTypeEnum;
import com.x.provider.api.oss.enums.SuggestionTypeEnum;
import com.x.provider.api.oss.model.ao.GreenRpcAO;
import com.x.provider.api.oss.service.GreenRpcService;
import com.x.provider.api.video.constants.VideoEventTopic;
import com.x.provider.api.video.enums.VideoStatusEnum;
import com.x.provider.api.video.model.event.VideoChangedEvent;
import com.x.provider.api.vod.enums.ReviewResultEnum;
import com.x.provider.api.vod.model.ao.GetContentReviewResultAO;
import com.x.provider.api.vod.model.dto.ContentReviewResultDTO;
import com.x.provider.api.vod.model.dto.MediaInfoDTO;
import com.x.provider.api.vod.service.VodRpcService;
import com.x.provider.video.constant.Constants;
import com.x.provider.video.enums.VideoErrorEnum;
import com.x.provider.video.mapper.VideoAttributeMapper;
import com.x.provider.video.mapper.VideoMapper;
import com.x.provider.video.mapper.VideoTopicMapper;
import com.x.provider.video.model.ao.homepage.CreateVideoAO;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoAttribute;
import com.x.provider.video.model.domain.VideoTopic;
import com.x.provider.video.service.RedisKeyService;
import com.x.provider.video.service.TopicService;
import com.x.provider.video.service.VideoService;
import com.x.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public VideoServiceImpl(VideoMapper videoMapper,
                            TopicService topicService,
                            VideoTopicMapper videoTopicMapper,
                            VodRpcService vodRpcService,
                            Executor executor,
                            VideoAttributeMapper videoAttributeMapper,
                            GreenRpcService greenRpcService,
                            RedisKeyService redisKeyService,
                            RedisService redisService,
                            KafkaTemplate<String, Object> kafkaTemplate){
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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createVideo(CreateVideoAO createVideoAO, long customerId) {
        R<String> greenResult = greenRpcService.greenSync(GreenRpcAO.builder().dataType(GreenDataTypeEnum.TEXT.name()).value(createVideoAO.getTitle()).build());
        ApiAssetUtil.isTrue(SuggestionTypeEnum.PASS.name().equals(greenResult.getData()), VideoErrorEnum.VIDEO_TITLE_REVIEW_BLOCKED);
        var topicTitleList = parseVideoTopicTitle(createVideoAO.getTitle());
        var topicList = topicService.listOrCreateTopics(topicTitleList);
        ApiAssetUtil.isTrue(topicList.size() == topicList.size());
        Video video = Video.builder().customerId(customerId).fileId(createVideoAO.getFileId()).reviewed(false).title(createVideoAO.getTitle()).videoStatus(VideoStatusEnum.REVIEW.ordinal()).build();
        videoMapper.insert(video);
        topicList.forEach(item -> {
            videoTopicMapper.insert(VideoTopic.builder().topicId(item.getId()).videoId(video.getId()).videoStatus(video.getVideoStatus()).build());
        });
        //以后这个地方改为kafka
        executor.execute(() -> {
            vodRpcService.contentReview(GetContentReviewResultAO.builder().fileIds(Arrays.asList(video.getFileId())).notifyUrl(Constants.VIDEO_REVIEW_NOTIFY_RUL).build());
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
        VideoStatusEnum videoStatus = ReviewResultEnum.BLOCK.name().equals(contentReviewResultDTO.getReviewResult()) ? VideoStatusEnum.UNPUBLISH : VideoStatusEnum.PUBLISH;
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
            //TODO:发送消息通知用户审核失败
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
    public IPage<Video> listVideo(long customerId, IPage page){
        return videoMapper.selectPage(page, new LambdaQueryWrapper<Video>().eq(Video::getCustomerId, customerId).eq(Video::getVideoStatus, VideoStatusEnum.PUBLISH.ordinal()).orderByDesc(Video::getTopValue).orderByDesc(Video::getCreatedOnUtc));
    }

    @Override
    public List<Video> listVideo(List<Long> ids) {
        return videoMapper.selectList(new LambdaQueryWrapper<Video>().in(Video::getId, ids));
    }

    @Override
    public void onStar(StarEvent starEvent) {
        if (starEvent.getItemType().equals(StarItemTypeEnum.VIDEO.getValue())){
            if(starEvent.isStar()) {
                addVideoStarList(Long.parseLong(starEvent.getItemId()), starEvent.getStarCustomerId());
                return;
            }
            removeFromVideoStarList(Long.parseLong(starEvent.getItemId()), starEvent.getStarCustomerId());
        }
    }

    @Override
    public List<Video> listCustomerStarVideo(PageDomain pageDomain, long starCustomerId) {
        Set<Long> starVideoIds = redisService.reverseRangeLong(redisKeyService.getCustomerStarVideoKey(starCustomerId), pageDomain.getPageNum(), pageDomain.getPageSize());
        if (starVideoIds.size() == 0){
            return Collections.emptyList();
        }
        List<Video> videos = listVideo(starVideoIds);
        if (starVideoIds.size() != videos.size()){
            starVideoIds.removeAll(videos.stream().map(Video::getId).collect(Collectors.toSet()));
            starVideoIds.parallelStream().forEach(item -> {
                removeFromVideoStarList(item, starCustomerId);
            });
            return listCustomerStarVideo(pageDomain, starCustomerId);
        }
        return videos;
    }

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

    public List<Video> listVideo(List<Long> customerIds, Date afterUpdateDate){
        return videoMapper.selectList(new LambdaQueryWrapper<Video>().in(Video::getCustomerId, customerIds).eq(Video::getVideoStatus, VideoStatusEnum.PUBLISH.ordinal()).ge(Video::getUpdatedOnUtc, afterUpdateDate));
    }

    private void sendVideoChangedEvent(Video video, VideoChangedEvent.EventTypeEnum eventTypeEnum){
        VideoChangedEvent changedEvent = BeanUtil.prepare(video, VideoChangedEvent.class);
        changedEvent.setEventType(eventTypeEnum.getValue());
        kafkaTemplate.send(VideoEventTopic.TOPIC_NAME_VIDEO_CHANGED, String.valueOf(video.getId()), changedEvent);
    }
}
