package com.x.provider.video.enums;

import java.time.Duration;

public enum VideoRecommendPoolEnum {
    SCREEN(1, 1, Duration.ofDays(2).getSeconds()),
    HOT(2, 5, Duration.ofDays(2).getSeconds()),
    VIDEO_HOT_TOPIC(2, 5, Duration.ofDays(2).getSeconds()),
    ;

    private int poolLevel;
    private long minScore;
    private int maxFreshSeconds;

    VideoRecommendPoolEnum(int poolLevel, long minScore, Long maxFresh){
        this.poolLevel = poolLevel;
        this.minScore = minScore;
        this.maxFreshSeconds = maxFreshSeconds;
    }

    public int getPoolLevel() {
        return poolLevel;
    }

    public int getMaxFreshSeconds() {
        return maxFreshSeconds;
    }

    public long getMinScore() {
        return minScore;
    }
}
