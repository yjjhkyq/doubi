package com.paascloud.redis.service;

import java.util.UUID;

public class DistributeRedisLock implements AutoCloseable {
    private String lockValue = UUID.randomUUID().toString();
    private String lockKey;
    private final String LOCK_KEY_PREFIX = "DistributeLock:";
    private final RedisService redisService;

    public DistributeRedisLock(String key){
        lockKey = LOCK_KEY_PREFIX + key;
        redisService = SpringService.getBean(RedisService.class);
        if (!redisService.tryLock(lockKey, lockValue)){
            throw new IllegalStateException("get redis lock failed");
        }
    }

    @Override
    public void close() {
        redisService.unlock(lockKey, lockValue);
    }
}
