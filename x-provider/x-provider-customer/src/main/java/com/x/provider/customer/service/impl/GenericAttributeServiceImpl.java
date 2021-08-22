package com.x.provider.customer.service.impl;

import com.x.core.constant.Constants;
import com.x.provider.customer.model.domain.GenericAttribute;
import com.x.provider.customer.service.GenericAttributeService;
import com.x.provider.customer.service.RedisKeyService;
import com.x.redis.service.RedisService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GenericAttributeServiceImpl implements GenericAttributeService {

    private final RedisService redisService;
    private final RedisKeyService redisKeyService;

    public GenericAttributeServiceImpl(RedisService redisService,
                                       RedisKeyService redisKeyService){
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
    }

    @Override
    public List<GenericAttribute> listAttribute(String keyGroup, long entityId) {
        throw new IllegalStateException("not supported");
    }

    @Override
    public Map<String, String> listAttributeMap(String keyGroup, long entityId) {
        Map<String, String> result = redisService.getCacheMap(redisKeyService.getGenricAttributeHashKey(entityId, keyGroup));
        return Optional.ofNullable(result).orElse(new HashMap<>());
    }

    @Override
    public GenericAttribute getAttribute(String keyGroup, long entityId, String key) {
        throw new IllegalStateException("not supported");
    }

    @Override
    public String getAttributeValue(String keyGroup, long entityId, String key) {
        return redisService.getCacheMapValue(redisKeyService.getGenricAttributeHashKey(entityId, keyGroup), key);
    }

    @Override
    public void addOrUpdateAttribute(String keyGroup, long entityId, String key, String value) {
        redisService.setCacheMapValue(redisKeyService.getGenricAttributeHashKey(entityId, keyGroup), key, value);
    }

    @Override
    public void addOrUpdateDraftAttribute(String keyGroup, long entityId, String key, String value) {
        redisService.setCacheMapValue(redisKeyService.getGenricAttributeHashKey(entityId, keyGroup), Constants.getDraftAttributeName(key), value);
    }

    @Override
    public void deleteDraftAttribute(String keyGroup, long entityId, String key) {
        redisService.deleteCacheMapValue(redisKeyService.getGenricAttributeHashKey(entityId, keyGroup),  Constants.getDraftAttributeName(key));
    }
}
