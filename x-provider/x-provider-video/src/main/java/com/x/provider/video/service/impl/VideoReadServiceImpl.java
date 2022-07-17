package com.x.provider.video.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageHelper;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.video.configure.ApplicationConfig;
import com.x.provider.video.enums.FollowVideoTypeEnum;
import com.x.provider.video.model.domain.TopicCustomerFavorite;
import com.x.provider.video.model.domain.Video;
import com.x.provider.video.model.domain.VideoRecommendPool;
import com.x.provider.video.model.domain.VideoRecommendPoolHotTopic;
import com.x.provider.video.service.*;
import com.x.redis.domain.LongTypeTuple;
import com.x.redis.service.RedisService;
import com.x.util.ListUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
    public PageList<Video> listMyFollowPersonVideo(PageDomain pageDomain, Long customerId) {
        Set<Long> videoIdList = redisService.dynamicPage(redisKeyService.getMyFollowVideoKey(FollowVideoTypeEnum.PERSON, customerId), 1, TimeUnit.DAYS, pageDomain.getPageNum(), pageDomain.getPageSize(), () -> {
            Date followVideoEndTime = new Date();
            Date followVideoInitTime = getFollowVideoInitTime(FollowVideoTypeEnum.PERSON, customerId);
            boolean canInit = DateUtils.addMinutes(new Date(), - applicationConfig.getFollowVideoInitMinIntervalMinute()).after(followVideoInitTime);
            if (!canInit){
                return Collections.emptySet();
            }
            setFollowVideoInitTime(FollowVideoTypeEnum.PERSON, customerId, followVideoEndTime);
            List<Long> follows = customerRpcService.listFollow(customerId).getData();
            if (follows.isEmpty()){
                return Collections.emptySet();
            }
            Set<ZSetOperations.TypedTuple> initData = new HashSet<ZSetOperations.TypedTuple>(1000);
            ListUtil.pageConsume(follows, applicationConfig.getSqlInMaxElement(), (t) -> {
                videoService.listVideo(t, followVideoInitTime).stream().forEach(item -> {
                    initData.add(new LongTypeTuple(item.getId(), item.getId().doubleValue()));
                });
            });
            return initData;

        });
        return PageHelper.buildPageList(pageDomain, videoService.listVideo(new ArrayList<>(videoIdList)), !videoIdList.isEmpty());
    }

    @Override
    public PageList<Video> listMyFollowTopicHotVideo(PageDomain pageDomain, Long customerId) {
        Set<Long> videoIdList = redisService.dynamicPage(redisKeyService.getMyFollowVideoKey(FollowVideoTypeEnum.SECURITY, customerId), 1, TimeUnit.DAYS, pageDomain.getPageNum(), pageDomain.getPageSize(), () -> {
            List<TopicCustomerFavorite> topicCustomerFavorites = topicService.listTopicCustomerFavorite(customerId, null);
            if (topicCustomerFavorites.isEmpty()){
                return Collections.emptySet();
            }
            Set<Long> videos = new HashSet<>(applicationConfig.getFollowTopicHotVideoShowSize());
            IPage page = new Page(0, applicationConfig.getFollowTopicHotVideoShowSize(), false);
            List<Long> followTopics = topicCustomerFavorites.stream().map(TopicCustomerFavorite::getTopicId).collect(Collectors.toList());
            Set<ZSetOperations.TypedTuple> initData = new HashSet<ZSetOperations.TypedTuple>(1000);
//            while (videos.size() < applicationConfig.getFollowTopicHotVideoShowSize()){
                IPage<VideoRecommendPoolHotTopic> videoHotTopicList = hotTopicVideoReadService.selectPage(page, followTopics);
                if (videoHotTopicList.getRecords().size() == 0){
                    return initData;
                }
                videoHotTopicList.getRecords().stream().forEach(item -> {
                    initData.add(new LongTypeTuple(item.getVideoId(), item.getVideoId().doubleValue()));
                });
//            }
            return initData;
        });
        return PageHelper.buildPageList(pageDomain, videoService.listVideo(new ArrayList<>(videoIdList)), !videoIdList.isEmpty());
    }

    @Override
    public PageList<Video> listHotVideoTopic(PageDomain pageDomain, Long topicId){
        List<VideoRecommendPoolHotTopic> result = hotTopicVideoReadService.selectPage(PageHelper.buildIPageRequest(pageDomain),
                Arrays.asList(topicId)).getRecords().stream().filter(item -> item.getVideoId() >= pageDomain.getCursor()).collect(Collectors.toList());
        if (result.isEmpty()){
            return new PageList<>();
        }
        List<VideoRecommendPoolHotTopic> videoRecommendPoolHotTopics = result.subList(0, pageDomain.getPageSize() > result.size() ? result.size() : pageDomain.getPageSize());
        if (videoRecommendPoolHotTopics.isEmpty()){
            return new PageList<>();
        }
        List<Video> videoList = videoService.listVideo(result.stream().map(item -> item.getVideoId()).collect(Collectors.toList()));
        return PageHelper.buildPageList(pageDomain, videoList, !result.isEmpty());
    }

    @Override
    public PageList<Video> listScreenVideo(PageDomain pageDomain) {
        PageList<VideoRecommendPool> result = videoRecommendPoolReadService.listScreen(pageDomain);
        if (result.isEmptyList()){
            return new PageList<>();
        }
        List<Video> videos = videoService.listVideo(result.getList().stream().map(item -> item.getVideoId()).collect(Collectors.toList()));
        return result.map(videos);
    }

    @Override
    public PageList<Video> listHotVideo(PageDomain pageDomain) {
        PageList<VideoRecommendPool> result = videoRecommendPoolReadService.listHot(pageDomain);
        if (result.isEmptyList()){
            return new PageList<>();
        }
        List<Video> videoList = videoService.listVideo(result.getList().stream().map(item -> item.getVideoId()).collect(Collectors.toList()));
        return result.map(videoList);
    }

    private Date getFollowVideoInitTime(FollowVideoTypeEnum followVideoTypeEnum, long customerId){
        Date defaultInitTime = DateUtils.addDays(new Date(), - applicationConfig.getFollowVideoDefaultInitIntervalDay());
//        Optional<Long> initTimeOptional = redisService.getCacheMapValueOptional(redisKeyService.getMyFollowVideoInitTimeKey(), redisKeyService.getMyFollowVideoInitTimeHashKey(followVideoTypeEnum, customerId));
//        if (initTimeOptional.isPresent() && defaultInitTime.getTime() <= initTimeOptional.get()){
//            return new Date(initTimeOptional.get());
//        }
        return defaultInitTime;
    }

    private void setFollowVideoInitTime(FollowVideoTypeEnum followVideoInitTime, long customerId, Date initDate){
        redisService.setCacheMapValue(redisKeyService.getMyFollowVideoInitTimeKey(), redisKeyService.getMyFollowVideoInitTimeHashKey(followVideoInitTime, customerId), initDate.getTime());
    }

    private void incVideoScore(Long videoId, Long score){

    }
}
