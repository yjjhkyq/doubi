package com.x.provider.video.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.ApiAssetUtil;
import com.x.provider.api.video.enums.VideoStatusEnum;
import com.x.provider.api.vod.enums.ReviewResultEnum;
import com.x.provider.api.vod.model.ao.GetContentReviewResultAO;
import com.x.provider.api.vod.model.dto.ContentReviewResultDTO;
import com.x.provider.api.vod.service.VodRpcService;
import com.x.provider.video.constant.Constants;
import com.x.provider.video.mapper.VideoAttributeMapper;
import com.x.provider.video.mapper.VideoMapper;
import com.x.provider.video.mapper.VideoTopicMapper;
import com.x.provider.video.model.ao.CreateVideoAO;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoAttribute;
import com.x.provider.video.model.domain.VideoTopic;
import com.x.provider.video.service.TopicService;
import com.x.provider.video.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

    public VideoServiceImpl(VideoMapper videoMapper,
                            TopicService topicService,
                            VideoTopicMapper videoTopicMapper,
                            VodRpcService vodRpcService,
                            Executor executor,
                            VideoAttributeMapper videoAttributeMapper){
        this.videoMapper = videoMapper;
        this.topicService = topicService;
        this.videoTopicMapper = videoTopicMapper;
        this.vodRpcService = vodRpcService;
        this.executor = executor;
        this.videoAttributeMapper = videoAttributeMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createVideo(CreateVideoAO createVideoAO, long customerId) {
        var topicTitleList = parseVideoTopicTitle(createVideoAO.getTitle());
        var topicList = topicService.listTopic(topicTitleList);
        ApiAssetUtil.isTrue(topicList.size() == topicList.size());
        Video video = Video.builder().customerId(customerId).fileId(createVideoAO.getFileId()).reviewed(false).title(createVideoAO.getTitle()).videoStatus(VideoStatusEnum.REVIEW.ordinal()).build();
        videoMapper.insert(video);
        var videoTopics = new ArrayList<VideoTopic>(topicList.size());
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
        videoMapper.deleteById(id);
        videoTopicMapper.delete(new LambdaQueryWrapper<VideoTopic>().eq(VideoTopic::getVideoId, id));
    }

    public void onVodContentReview(ContentReviewResultDTO contentReviewResultDTO){
        var videoOpt = getVideo(contentReviewResultDTO.getFileId());
        if (videoOpt.isEmpty()){
            log.error("no video find, file id:{}", contentReviewResultDTO.getFileId());
            return;
        }
        VideoStatusEnum videoStatus = ReviewResultEnum.BLOCK.equals(contentReviewResultDTO.getReviewResult()) ? VideoStatusEnum.UNPUBLISH : VideoStatusEnum.PUBLISH;
        Video video = videoOpt.get();
        video.setReviewed(true);
        var videoTopics = videoTopicMapper.selectList(new LambdaQueryWrapper<VideoTopic>().eq(VideoTopic::getVideoId, video.getId()));
        if (VideoStatusEnum.PUBLISH == videoStatus) {
            video.setVideoStatus(videoStatus.ordinal());
            videoMapper.updateById(video);
            videoTopics.forEach(item -> {
                item.setVideoStatus(video.getVideoStatus());
                videoTopicMapper.updateById(item);
            });
        }
        else{
            videoMapper.deleteById(video.getId());
            videoTopics.forEach(item -> {
                videoTopicMapper.deleteBatchIds(videoTopics.stream().map(VideoTopic::getId).collect(Collectors.toList()));
            });
            //TODO:发送消息通知用户审核失败
        }
    }

    @Override
    public void topMyVideo(long videoId, boolean top, long customerId) {
        var videoOpt = getVideo(videoId, null);
        ApiAssetUtil.isTrue(videoOpt.isPresent() && videoOpt.get().getCustomerId() == customerId);
        var videoAttributeOpt = getVideoAttribute(videoId);
        if (videoAttributeOpt.isPresent()){
            videoAttributeOpt.get().setTop(top);
            if (top){
                videoAttributeOpt.get().setTopValue(System.currentTimeMillis());
            }
            videoAttributeMapper.updateById(videoAttributeOpt.get());
            return;
        }
        videoAttributeMapper.insert(VideoAttribute.builder().top(true).topValue(System.currentTimeMillis()).videoId(videoId).build());
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
        return Optional.ofNullable(videoMapper.selectOne(query));
    }

    private List<String> parseVideoTopicTitle(String text){
        String[] splitText = text.split(" ");
        List<String> result = new ArrayList<>(splitText.length);
        Arrays.stream(splitText).filter(item -> item.startsWith(TOPIC_PREFIX)).forEach(item ->{
            result.add(item.replaceFirst(TOPIC_PREFIX, ""));
        });
        return result;
    }
}
