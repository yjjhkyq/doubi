package com.x.provider.video.service.impl.recmmend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.utils.DateUtils;
import com.x.provider.video.enums.VideoRecommendPoolEnum;
import com.x.provider.video.mapper.VideoRecommendPoolHotTopicMapper;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoRecommendPoolHotTopic;
import com.x.provider.video.model.domain.VideoTopic;
import com.x.provider.video.service.HotTopicVideoReadService;
import com.x.provider.video.service.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VideoRecommendPoolHotTopicServiceImpl extends BaseVideoRecommendPoolService implements HotTopicVideoReadService {

    private final VideoService videoService;
    private final VideoRecommendPoolHotTopicMapper hotVideoInTopicMapper;

    public VideoRecommendPoolHotTopicServiceImpl(VideoService videoService,
                                                 VideoRecommendPoolHotTopicMapper videoRecommendPoolHotTopicMapper){
        this.videoService = videoService;
        this.hotVideoInTopicMapper = videoRecommendPoolHotTopicMapper;
    }

    @Override
    @Transactional
    public void onVideoScoreChanged(Long videoId, Long score) {
        Optional<Video> video = videoService.getVideo(videoId);
        if (video.isEmpty()){
            return;
        }
        Optional<Integer> videoRecommendPoolLevel = getVideoRecommendPoolLevel(score);
        if (videoRecommendPoolLevel.isEmpty() || videoRecommendPoolLevel.get() <= VideoRecommendPoolEnum.VIDEO_HOT_TOPIC.getPoolLevel()){
            return;
        }
        List<VideoRecommendPoolHotTopic> existed = getVideoRecommendPoolHotTopic(videoId);
        if (existed.isEmpty()){
            List<VideoTopic> videoTopics = videoService.listVideoTopic(videoId);
            Date createDate = DateUtils.today();
            videoTopics.forEach(item -> {
                hotVideoInTopicMapper.insert(VideoRecommendPoolHotTopic.builder().videoId(videoId).topicId(item.getTopicId()).Score(score).createOnDate(createDate).build());
            });
            return;
        }
        existed.forEach(item -> {
            hotVideoInTopicMapper.updateById(VideoRecommendPoolHotTopic.builder().id(item.getId()).Score(score).build());
        });
    }

    @Override
    public void onVideoDeleted(Long videoId) {
        List<VideoRecommendPoolHotTopic> deleteList = getVideoRecommendPoolHotTopic(videoId);
        if (!deleteList.isEmpty()) {
            hotVideoInTopicMapper.deleteBatchIds(deleteList.stream().map(VideoRecommendPoolHotTopic::getId).collect(Collectors.toList()));
        }
    }

    private List<VideoRecommendPoolHotTopic> getVideoRecommendPoolHotTopic(Long videoId){
        LambdaQueryWrapper<VideoRecommendPoolHotTopic> lambdaQueryWrapper = new LambdaQueryWrapper<VideoRecommendPoolHotTopic>();
        if (videoId != null){
            lambdaQueryWrapper = lambdaQueryWrapper.eq(VideoRecommendPoolHotTopic::getVideoId, videoId);
        }
        return hotVideoInTopicMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public IPage<VideoRecommendPoolHotTopic> selectPage(IPage page, List<Long> followTopics) {
        return hotVideoInTopicMapper.selectPage(page, new LambdaQueryWrapper<VideoRecommendPoolHotTopic>().in(VideoRecommendPoolHotTopic::getTopicId, followTopics)
                .orderByDesc(VideoRecommendPoolHotTopic::getCreateOnDate).orderByDesc(VideoRecommendPoolHotTopic::getScore)
                .orderByDesc(VideoRecommendPoolHotTopic::getVideoId));
    }
}
