package com.x.provider.video.service.recmmend;

import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.api.statistic.model.event.MetricValueChangedEvent;
import com.x.provider.api.video.model.event.VideoPlayEvent;

public interface VideoRecommendService {
    void onStar(StarEvent starEvent);
    void onPlay(VideoPlayEvent videoPlayEvent);
    void onStatisticTotalChanged(MetricValueChangedEvent statisticTotalChangedEvent);
    void onVideoDeleted(Long videoId);
}
