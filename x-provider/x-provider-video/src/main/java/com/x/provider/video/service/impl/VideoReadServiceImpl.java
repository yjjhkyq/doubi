package com.x.provider.video.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.video.configure.ApplicationConfig;
import com.x.provider.video.enums.FollowVideoTypeEnum;
import com.x.provider.video.model.domain.TopicCustomerFavorite;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoRecommendPool;
import com.x.provider.video.model.domain.VideoRecommendPoolHotTopic;
import com.x.provider.video.service.*;
import com.x.redis.service.RedisService;
import com.x.util.ListUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoReadServiceImpl implements VideoReadService {

    private final RedisKeyService redisKeyService;
    private final RedisService redisService;
    private final VideoService videoService;
    private final CustomerRpcService customerRpcService;
    private final ApplicationConfig applicationConfig;
    private final TopicService topicService;
    private final HotTopicVideoReadService hotTopicVideoReadService;
    private final VideoRecommendPoolReadService videoRecommendPoolReadService;

    public VideoReadServiceImpl(RedisKeyService redisKeyService,
                                RedisService redisService,
                                VideoService videoService,
                                CustomerRpcService customerRpcService,
                                ApplicationConfig applicationConfig,
                                TopicService topicService,
                                HotTopicVideoReadService hotTopicVideoReadService,
                                VideoRecommendPoolReadService videoRecommendPoolReadService){
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
        this.videoService = videoService;
        this.customerRpcService = customerRpcService;
        this.applicationConfig = applicationConfig;
        this.topicService = topicService;
        this.hotTopicVideoReadService = hotTopicVideoReadService;
        this.videoRecommendPoolReadService = videoRecommendPoolReadService;
    }

    @Override
    public List<Video> listMyFollowPersonVideo(Long customerId) {
        Date followVideoEndTime = new Date();
        Date followVideoInitTime = getFollowVideoInitTime(FollowVideoTypeEnum.PERSON, customerId);
        boolean canInit = DateUtils.addMinutes(new Date(), - applicationConfig.getFollowVideoInitMinIntervalMinute()).after(followVideoInitTime);
        if (!canInit){
            return Collections.emptyList();
        }
        List<Long> follows = customerRpcService.listFollow(customerId).getData();
        if (follows.isEmpty()){
            return Collections.emptyList();
        }
        List<Video> result = new ArrayList<>(1000);
        ListUtil.pageConsume(follows, applicationConfig.getSqlInMaxElement(), (t) -> {
            result.addAll(videoService.listVideo(t, followVideoInitTime));
        });
        setFollowVideoInitTime(FollowVideoTypeEnum.PERSON, customerId, followVideoEndTime);
        return result;
    }

    @Override
    public List<Long> listMyFollowTopicHotVideo(Long customerId) {
        List<TopicCustomerFavorite> topicCustomerFavorites = topicService.listTopicCustomerFavorite(customerId, null);
        if (topicCustomerFavorites.isEmpty()){
            return Collections.emptyList();
        }
        Set<Long> videos = new HashSet<>(applicationConfig.getFollowTopicHotVideoShowSize());
        IPage page = new Page(1, applicationConfig.getFollowTopicHotVideoShowSize(), false);
        List<Long> followTopics = topicCustomerFavorites.stream().map(TopicCustomerFavorite::getTopicId).collect(Collectors.toList());
        while (videos.size() < applicationConfig.getFollowTopicHotVideoShowSize()){
            IPage<VideoRecommendPoolHotTopic> videoHotTopicList = hotTopicVideoReadService.selectPage(page, followTopics);
            if (videoHotTopicList.getRecords().size() == 0){
                break;
            }
            videos.addAll(videoHotTopicList.getRecords().stream().map(VideoRecommendPoolHotTopic::getVideoId).collect(Collectors.toList()));
        }
        List<Long> result = new ArrayList<>(videos);
        if (result.size() >= applicationConfig.getFollowTopicHotVideoShowSize()){
            return result.subList(0, applicationConfig.getFollowTopicHotVideoShowSize());
        }
        return result;
    }

    @Override
    public List<VideoRecommendPoolHotTopic> listHotVideoTopic(Long topicId){
        IPage<VideoRecommendPoolHotTopic> result = hotTopicVideoReadService.selectPage(new Page<>(0, applicationConfig.getTopicHotVideoShowSize(), false), Arrays.asList(topicId));
        return result.getRecords();
    }

    @Override
    public PageList<VideoRecommendPool> listScreenVideo(PageDomain pageDomain) {
        return videoRecommendPoolReadService.listScreen(pageDomain);
    }

    @Override
    public List<VideoRecommendPool> listHotVideo() {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageSize(applicationConfig.getHotVideoShowSize());
        return videoRecommendPoolReadService.listHot(pageDomain).getList();
    }

    private Date getFollowVideoInitTime(FollowVideoTypeEnum followVideoTypeEnum, long customerId){
        Date defaultInitTime = DateUtils.addDays(new Date(), - applicationConfig.getFollowVideoDefaultInitIntervalDay());
        Optional<Long> initTimeOptional = redisService.getCacheMapValueOptional(redisKeyService.getMyFollowVideoInitTimeKey(), redisKeyService.getMyFollowVideoInitTimeHashKey(followVideoTypeEnum, customerId));
        if (initTimeOptional.isPresent() && defaultInitTime.getTime() <= initTimeOptional.get()){
            return new Date(initTimeOptional.get());
        }
        return defaultInitTime;
    }

    private void setFollowVideoInitTime(FollowVideoTypeEnum followVideoInitTime, long customerId, Date initDate){
        redisService.setCacheMapValue(redisKeyService.getMyFollowVideoInitTimeKey(), redisKeyService.getMyFollowVideoInitTimeHashKey(followVideoInitTime, customerId), initDate.getTime());
    }

    private void incVideoScore(Long videoId, Long score){

    }
}
