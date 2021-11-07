package com.x.provider.video.service.impl.recmmend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.DateUtils;
import com.x.provider.video.enums.VideoRecommendPoolEnum;
import com.x.provider.video.model.domain.VideoRecommendPool;
import com.x.provider.video.service.recmmend.VideoRecommendPoolService;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

public abstract class BaseVideoRecommendPoolService implements VideoRecommendPoolService {
    protected Optional<Integer> getVideoRecommendPoolLevel(Long score) {
        Long currentTime = System.currentTimeMillis();
        Date date = new Date();
        return Arrays.stream(VideoRecommendPoolEnum.values()).filter(item -> score.longValue() >= item.getMinScore() && currentTime >= DateUtils.addSeconds(date, item.getMaxFreshSeconds()).getTime())
                .map(item -> item.getPoolLevel()).max(Integer::compareTo);

    }
}
