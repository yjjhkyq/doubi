package com.x.provider.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.constant.Constants;
import com.x.provider.customer.mapper.GenericAttributeMapper;
import com.x.provider.customer.model.domain.GenericAttribute;
import com.x.provider.customer.service.GenericAttributeService;
import com.x.provider.customer.service.RedisKeyService;
import com.x.redis.service.RedisService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class GenericAttributeServiceImpl implements GenericAttributeService {

    private final RedisService redisService;
    private final RedisKeyService redisKeyService;
    private final GenericAttributeMapper genericAttributeMapper;

    public GenericAttributeServiceImpl(RedisService redisService,
                                       RedisKeyService redisKeyService,
                                       GenericAttributeMapper genericAttributeMapper){
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
        this.genericAttributeMapper = genericAttributeMapper;
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
        save(keyGroup, entityId, key, value);
        redisService.setCacheMapValue(redisKeyService.getGenricAttributeHashKey(entityId, keyGroup), key, value);
    }

    @Override
    public void addOrUpdateDraftAttribute(String keyGroup, long entityId, String key, String value) {
        save(keyGroup, entityId, Constants.getDraftAttributeName(key), value);
        redisService.setCacheMapValue(redisKeyService.getGenricAttributeHashKey(entityId, keyGroup), Constants.getDraftAttributeName(key), value);
    }

    @Override
    public void deleteDraftAttribute(String keyGroup, long entityId, String key) {
        GenericAttribute genericAttribute = getBy(key, entityId, key);
        if (genericAttribute == null){
            return;
        }
        genericAttributeMapper.deleteById(genericAttribute.getId());
        redisService.deleteCacheMapValue(redisKeyService.getGenricAttributeHashKey(entityId, keyGroup),  Constants.getDraftAttributeName(key));
    }

    @Override
    public Map<Long, Map<String, String>> listAttributeMap(String keyGroup, List<Long> entityIdList) {
        LambdaQueryWrapper<GenericAttribute> query = buildQuery(keyGroup, 0L, entityIdList, null);
        List<GenericAttribute> genericAttributes = genericAttributeMapper.selectList(query);
        return prepare(genericAttributes);
    }

    private Map<Long, Map<String, String>> prepare(List<GenericAttribute> source){
        if (source.isEmpty()){
            return new HashMap<>();
        }
        Map<Long, Map<String, String>> result = new HashMap<>();
        source.stream().forEach(item -> {
            if (!result.containsKey(item.getEntityId())){
                result.put(item.getEntityId(), new HashMap<>());
            }
            result.get(item.getEntityId()).put(item.getKey(), item.getValue());
        });
        return result;
    }

    private void save(String keyGroup, long entityId, String key, String value){
        GenericAttribute genericAttribute = getBy(key, entityId, key);
        if (genericAttribute == null){
            genericAttribute = GenericAttribute.builder().entityId(entityId).key(key).keyGroup(keyGroup).value(value).build();
        }
        genericAttribute.setValue(value);
        if (genericAttribute.getId() > 0){
            genericAttributeMapper.updateById(genericAttribute);
        }
        else{
            genericAttributeMapper.insert(genericAttribute);
        }
    }

    private GenericAttribute getBy(String keyGroup, long entityId, String key){
        return genericAttributeMapper.selectOne(buildQuery(keyGroup, entityId, null, key));
    }

    private LambdaQueryWrapper<GenericAttribute> buildQuery(String keyGroup, long entityId, List<Long> entityIdList, String key){
        LambdaQueryWrapper<GenericAttribute> result = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(keyGroup)){
            result.eq(GenericAttribute::getKeyGroup, keyGroup);
        }
        if (entityId > 0){
            result.eq(GenericAttribute::getEntityId, entityId);
        }
        if (!CollectionUtils.isEmpty(entityIdList)){
            result.in(GenericAttribute::getEntityId, entityIdList);
        }
        if (!StringUtils.isEmpty(key)){
            result.eq(GenericAttribute::getKey, key);
        }
        return result;
    }
}
