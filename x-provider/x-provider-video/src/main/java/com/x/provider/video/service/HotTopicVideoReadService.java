package com.x.provider.video.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.x.provider.video.model.domain.VideoRecommendPoolHotTopic;

import java.util.List;

public interface HotTopicVideoReadService {
    IPage<VideoRecommendPoolHotTopic> selectPage(IPage page,  List<Long> followTopics);
}
