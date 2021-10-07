package com.x.provider.statistic.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.provider.statistic.service.RedisKeyService;
import org.springframework.stereotype.Service;

@Service
public class RedisKeyServiceImpl implements RedisKeyService {

    private static final String FULL_KEY = "Statistic:{}";

    private static final String STAT_TOTAL_KEY = "Total:{}";

    @Override
    public String getStatisticTotalKey(String key) {
        return getFullKey(STAT_TOTAL_KEY, key);
    }

    private String getFullKey(CharSequence keyTemplate, Object... params){
        return StrUtil.format(FULL_KEY, StrUtil.format(keyTemplate, params));
    }



}
