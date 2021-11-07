package com.x.provider.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.provider.video.model.domain.Topic;
import com.x.provider.video.model.domain.VideoPlayMetric;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoPlayMetricMapper extends BaseMapper<VideoPlayMetric> {
}
