package com.x.provider.vod.service.cache;

import com.x.provider.vod.service.RedisKeyService;
import com.x.redis.service.RedisService;
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
