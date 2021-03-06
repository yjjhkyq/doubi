package com.x.provider.video.service.impl.recmmend;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.DateUtils;
import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.video.enums.VideoRecommendPoolEnum;
import com.x.provider.video.mapper.VideoRecommendPoolMapper;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoRecommendPool;
import com.x.provider.video.service.VideoRecommendPoolReadService;
import com.x.provider.video.service.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
public class VideoRecommendPoolServiceImpl extends BaseVideoRecommendPoolService implements VideoRecommendPoolReadService {

    private final VideoService videoService;
    private final VideoRecommendPoolMapper videoRecommendPoolMapper;

    public VideoRecommendPoolServiceImpl(VideoService videoService,
                                         VideoRecommendPoolMapper videoRecommendPoolMapper){
        this.videoService = videoService;
        this.videoRecommendPoolMapper = videoRecommendPoolMapper;
    }

    @Override
    public void onVideoScoreChanged(Long videoId, Long score) {
        Optional<Video> video = videoService.getVideo(videoId);
        if (video.isEmpty()){
            return;
        }
        Optional<Integer> videoRecommendPoolLevel = getVideoRecommendPoolLevel(score);
        if (videoRecommendPoolLevel.isEmpty()){
            return;
        }
        Optional<VideoRecommendPool> videoRecommendPool = getVideoRecommendPool(videoId);
        if (videoRecommendPool.isEmpty()){
            videoRecommendPoolMapper.insert(VideoRecommendPool.builder().videoId(videoId).poolLevel(videoRecommendPoolLevel.get()).score(score).createOnDate(DateUtils.today()).build());
            return;
        }
        VideoRecommendPool videoRecommendPoolNew = VideoRecommendPool.builder().id(videoRecommendPool.get().getId()).score(videoRecommendPool.get().getScore()).build();
        if (!videoRecommendPool.get().getPoolLevel().equals(videoRecommendPool.get().getPoolLevel())){
            videoRecommendPoolNew.setPoolLevel(videoRecommendPool.get().getPoolLevel());
        }
        videoRecommendPoolMapper.updateById(videoRecommendPoolNew);
    }

    @Override
    public void onVideoDeleted(Long videoId) {
        Optional<VideoRecommendPool> videoRecommendPool = getVideoRecommendPool(videoId);
        if (videoRecommendPool.isPresent()) {
            videoRecommendPoolMapper.deleteById(videoRecommendPool.get().getId());
        }
    }

    private Optional<VideoRecommendPool> getVideoRecommendPool(Long videoId){
        LambdaQueryWrapper<VideoRecommendPool> lambdaQueryWrapper = new LambdaQueryWrapper<VideoRecommendPool>();
        if (videoId != null){
            lambdaQueryWrapper = lambdaQueryWrapper.eq(VideoRecommendPool::getVideoId, videoId);
        }
        return Optional.ofNullable(videoRecommendPoolMapper.selectOne(lambdaQueryWrapper));
    }

    @Override
    public PageList<VideoRecommendPool> listScreen(PageDomain pageDomain) {
        LambdaQueryWrapper<VideoRecommendPool> query = new LambdaQueryWrapper<VideoRecommendPool>().eq(VideoRecommendPool::getPoolLevel, VideoRecommendPoolEnum.SCREEN.getPoolLevel())
                .orderByDesc(VideoRecommendPool::getCreateOnDate).orderByDesc(VideoRecommendPool::getId).last(StrUtil.format(" limit {} ", pageDomain.getPageSize()));
        if (pageDomain.getCursor() > 0){
            query.lt(VideoRecommendPool::getId, pageDomain.getCursor());
        }
        List<VideoRecommendPool> videoRecommendPools = videoRecommendPoolMapper.selectList(query);
        if (videoRecommendPools.isEmpty()){
            return new PageList<>();
        }
        return new PageList<>(videoRecommendPools, pageDomain.getPageSize(), CollectionUtils.lastElement(videoRecommendPools).getId());
    }

    @Override
    public PageList<VideoRecommendPool> listHot(PageDomain pageDomain) {
        LambdaQueryWrapper<VideoRecommendPool> query = new LambdaQueryWrapper<VideoRecommendPool>().eq(VideoRecommendPool::getPoolLevel, VideoRecommendPoolEnum.SCREEN.getPoolLevel())
                .orderByDesc(VideoRecommendPool::getCreateOnDate).orderByDesc(VideoRecommendPool::getId).orderByDesc(VideoRecommendPool::getScore).orderByDesc(VideoRecommendPool::getVideoId)
                .last(StrUtil.format(" limit {} ", pageDomain.getPageSize()));
        if (pageDomain.getCursor() > 0){
            query.lt(VideoRecommendPool::getId, pageDomain.getCursor());
        }
        List<VideoRecommendPool> videoRecommendPools = videoRecommendPoolMapper.selectList(query);
        if (videoRecommendPools.isEmpty()){
            return new PageList<>();
        }
        return new PageList<>(videoRecommendPools, pageDomain.getPageSize(), CollectionUtils.lastElement(videoRecommendPools).getId());

    }
}
