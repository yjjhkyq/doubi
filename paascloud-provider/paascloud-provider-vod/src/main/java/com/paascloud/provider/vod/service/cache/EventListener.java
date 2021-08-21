package com.paascloud.provider.vod.service.cache;

import com.paascloud.provider.vod.service.RedisKeyService;
import com.paascloud.redis.service.RedisService;
import org.springframework.stereotype.Component;

@Component
public class EventListener {

    public final RedisKeyService redisKeyService;
    private final RedisService redisService;

    public EventListener(RedisKeyService redisKeyService,
                         RedisService redisService) {
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
    }
}
