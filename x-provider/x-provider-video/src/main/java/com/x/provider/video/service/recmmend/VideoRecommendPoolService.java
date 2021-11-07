package com.x.provider.video.service.recmmend;

public interface VideoRecommendPoolService {
    void onVideoScoreChanged(Long videoId, Long score);
    void onVideoDeleted(Long videoId);
}
