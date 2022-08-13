package com.x.provider.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.constant.Constants;
import com.x.core.domain.SuggestionTypeEnum;
import com.x.core.utils.BeanUtil;
import com.x.provider.customer.mapper.GenericAttributeMapper;
import com.x.provider.customer.model.ao.AddOrUpdateAttributeAO;
import com.x.provider.customer.model.domain.GenericAttribute;
import com.x.provider.customer.model.query.GenericAttributeQuery;
import com.x.provider.customer.service.GenericAttributeService;
import com.x.provider.customer.service.RedisKeyService;
import com.x.redis.service.RedisService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<GenericAttribute> listAttributeMap(String keyGroup, Long entityId) {
        throw new IllegalStateException("not supported");
    }

    @Override
    public GenericAttribute getAttribute(String keyGroup, Long entityId, String key) {
        throw new IllegalStateException("not supported");
    }

    @Override
    public GenericAttribute addOrUpdateAttribute(AddOrUpdateAttributeAO addOrUpdateAttributeAO) {
        GenericAttributeQuery genericAttributeQuery = GenericAttributeQuery.builder()
                .key(addOrUpdateAttributeAO.getKey())
                .keyGroup(addOrUpdateAttributeAO.getKeyGroup())
                .entityId(addOrUpdateAttributeAO.getEntityId())
                .suggestionType(addOrUpdateAttributeAO.getSuggestionType())
                .build();
        GenericAttribute genericAttribute = getBy(genericAttributeQuery);
        if (genericAttribute == null){
            genericAttribute = BeanUtil.prepare(addOrUpdateAttributeAO, GenericAttribute.class);
        }
        genericAttribute.setValue(addOrUpdateAttributeAO.getValue());
        genericAttribute.setSuggestionType(addOrUpdateAttributeAO.getSuggestionType());
        addOrUpdate(genericAttribute);
        return genericAttribute;
    }

    @Override
    public GenericAttribute addOrUpdateDraftAttribute(String keyGroup, Long entityId, String key, String value) {
        return addOrUpdateAttribute(AddOrUpdateAttributeAO.builder().key(key).keyGroup(keyGroup).value(value).entityId(entityId).suggestionType(SuggestionTypeEnum.REVIEW.getValue()).build());
    }

    @Override
    public void deleteDraftAttribute(String keyGroup, Long entityId, String key) {
        final GenericAttribute genericAttribute = getBy(GenericAttributeQuery.builder().keyGroup(keyGroup).key(key).entityId(entityId).suggestionType(SuggestionTypeEnum.REVIEW.getValue()).build());
        if (genericAttribute != null){
            genericAttributeMapper.deleteById(genericAttribute.getId());
        }
    }

    @Override
    public List<GenericAttribute> listAttributeMap(String keyGroup, List<Long> entityIdList) {
        return genericAttributeMapper.selectList(buildQuery(GenericAttributeQuery.builder().keyGroup(keyGroup).entityIdList(entityIdList).build()));
    }

    public void addOrUpdate(GenericAttribute genericAttribute){
        if (genericAttribute.getId() != null){
            genericAttributeMapper.updateById(genericAttribute);
            return;
        }
        genericAttributeMapper.insert(genericAttribute);
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

    private void save(String keyGroup, Long entityId, String key, String value){
        GenericAttribute genericAttribute = getBy(keyGroup, entityId, key);
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

    private GenericAttribute getBy(String keyGroup, Long entityId, String key){
        return genericAttributeMapper.selectOne(buildQuery(GenericAttributeQuery.builder().keyGroup(keyGroup).entityId(entityId).key(key).build()));
    }

    private GenericAttribute getBy(GenericAttributeQuery genericAttributeQuery){
        return genericAttributeMapper.selectOne(buildQuery(genericAttributeQuery));
    }

    private LambdaQueryWrapper<GenericAttribute> buildQuery(GenericAttributeQuery genericAttributeQuery){
        LambdaQueryWrapper<GenericAttribute> result = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(genericAttributeQuery.getKeyGroup())){
            result.eq(GenericAttribute::getKeyGroup, genericAttributeQuery.getKeyGroup());
        }
        if (genericAttributeQuery.getEntityId() != null){
            result.eq(GenericAttribute::getEntityId, genericAttributeQuery.getEntityId());
        }
        if (!CollectionUtils.isEmpty(genericAttributeQuery.getEntityIdList())){
            result.in(GenericAttribute::getEntityId, genericAttributeQuery.getEntityIdList());
        }
        if (!StringUtils.isEmpty(genericAttributeQuery.getKey())){
            result.eq(GenericAttribute::getKey, genericAttributeQuery.getKey());
        }
        return result;
    }

}
