package com.x.provider.video.service;

import com.x.provider.video.model.domain.VideoPlayMetric;

import java.util.Optional;

public interface VideoMetricService {
    Optional<VideoPlayMetric> getVideoPlayMetric(long videoId, long customerId);
    void reportVideoPlayMetric(long videoId, long customerId, int playDuration);
    void star(long starCustomerId, long starVideoId, boolean star);
}
