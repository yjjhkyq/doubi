package com.x.provider.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.cache.event.EntityChangedEventBus;
import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.customer.mapper.CustomerRelationMapper;
import com.x.provider.customer.model.domain.CustomerRelation;
import com.x.provider.customer.service.CustomerRelationService;
import com.x.provider.customer.service.RedisKeyService;
import com.x.provider.customer.service.cache.customer.CustomerRelationChangedEvent;
import com.x.redis.service.RedisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerRelationServiceImpl implements CustomerRelationService {

    private final RedisService redisService;
    private final RedisKeyService redisKeyService;
    private final CustomerRelationMapper customerRelationMapper;
    private final EntityChangedEventBus entityChangedEventBus;
    public CustomerRelationServiceImpl(RedisService redisService,
                                       RedisKeyService redisKeyService,
                                       CustomerRelationMapper customerRelationMapper,
                                       EntityChangedEventBus entityChangedEventBus){
        this.redisService = redisService;
        this.redisKeyService = redisKeyService;
        this.customerRelationMapper = customerRelationMapper;
        this.entityChangedEventBus = entityChangedEventBus;
    }

    @Override
    public CustomerRelationEnum getRelation(long fromCustomerId, long toCustomerId) {
        Optional<CustomerRelation> customerRelation = getCacheCustomerRelation(fromCustomerId, toCustomerId);
        if (!customerRelation.isPresent()){
            return CustomerRelationEnum.NO_RELATION;
        }
        return CustomerRelationEnum.valueOf(customerRelation.get().getRelation());
    }

    @Override
    @Transactional
    public void following(long fromCustomerId, long toCustomerId) {
        redisService.zadd(redisKeyService.getCustomerRelationOfFollowKey(fromCustomerId), toCustomerId, System.currentTimeMillis());
        redisService.zadd(redisKeyService.getCustomerRelationOfFansKey(toCustomerId),fromCustomerId, System.currentTimeMillis());
        CustomerRelation customerRelationFollow = getCustomerRelation(fromCustomerId, toCustomerId);
        if (customerRelationFollow != null){
            entityChangedEventBus.postEntityUpdated(new CustomerRelationChangedEvent(customerRelationFollow));
            return;
        }
        customerRelationFollow = CustomerRelation.builder().fromCustomerId(fromCustomerId).toCustomerId(toCustomerId).relation(CustomerRelationEnum.FOLLOW.getValue()).build();
        CustomerRelation customerRelationFan = getCustomerRelation(toCustomerId, fromCustomerId);
        if (customerRelationFan != null){
            customerRelationFollow.setRelation(CustomerRelationEnum.FRIEND.getValue());
            customerRelationFan.setRelation(CustomerRelationEnum.FRIEND.getValue());
            customerRelationMapper.updateById(customerRelationFan);
            entityChangedEventBus.postEntityUpdated(new CustomerRelationChangedEvent(customerRelationFan));
        }
        customerRelationMapper.insert(customerRelationFollow);
        entityChangedEventBus.postEntityInserted(new CustomerRelationChangedEvent(customerRelationFollow));
    }

    @Override
    @Transactional
    public void unFollowing(long fromCustomerId, long toCustomerId) {
        redisService.zremove(redisKeyService.getCustomerRelationOfFollowKey(fromCustomerId), toCustomerId);
        redisService.zremove(redisKeyService.getCustomerRelationOfFansKey(toCustomerId), fromCustomerId);
        CustomerRelation customerRelationFollow = getCustomerRelation(fromCustomerId, toCustomerId);
        if (customerRelationFollow == null){
            return;
        }
        customerRelationFollow.setRelation(CustomerRelationEnum.NO_RELATION.getValue());
        customerRelationMapper.updateById(customerRelationFollow);
        entityChangedEventBus.postEntityUpdated(new CustomerRelationChangedEvent(customerRelationFollow));
        CustomerRelation customerRelationFan = getCustomerRelation(toCustomerId, fromCustomerId);
        if (customerRelationFan != null && CustomerRelationEnum.FRIEND.getValue() == customerRelationFan.getRelation()){
            customerRelationFan.setRelation(CustomerRelationEnum.FOLLOW.getValue());
            customerRelationMapper.updateById(customerRelationFan);
            entityChangedEventBus.postEntityUpdated(new CustomerRelationChangedEvent(customerRelationFan));
        }
    }

    @Override
    public List<CustomerRelation> listFollow(long customerId, long page, long limit) {
        Set<Long> follows = redisService.reverseRangeLong(redisKeyService.getCustomerRelationOfFollowKey(customerId), page, limit);
        if (follows.size() <= 0){
            return Collections.emptyList();
        }
        List<String> keys = new ArrayList<>(follows.size());
        follows.forEach(item -> {
            keys.add(redisKeyService.getCustomerRelationKey(customerId, item));
        });
        return redisService.listCacheObject(keys);
    }

    @Override
    public List<Long> listFollow(long customerId) {
        return redisService.rangeLong((redisKeyService.getCustomerRelationOfFollowKey(customerId))).stream().collect(Collectors.toList());
    }

    @Override
    public List<CustomerRelation> listFans(long customerId, long page, long limit) {
        Set<Long> fans = redisService.reverseRangeLong(redisKeyService.getCustomerRelationOfFansKey(customerId), page, limit);
        List<String> keys = new ArrayList<>(fans.size());
        fans.forEach(item -> {
            keys.add(redisKeyService.getCustomerRelationKey(item, customerId));
        });
        return redisService.listCacheObject(keys);
    }

    @Override
    public long getFansCount(long customerId) {
        return redisService.zsize(redisKeyService.getCustomerRelationOfFansKey(customerId));
    }

    @Override
    public long getFollowCount(long customerId) {
        return redisService.zsize(redisKeyService.getCustomerRelationOfFollowKey(customerId));
    }

    public Optional<CustomerRelation> getCacheCustomerRelation(long fromCustomerId, long toCustomerId){
        CustomerRelation customerRelation = redisService.getCacheObject(redisKeyService.getCustomerRelationKey(fromCustomerId, toCustomerId), () -> getCustomerRelation(fromCustomerId, toCustomerId));
        return Optional.ofNullable(customerRelation);
    }

    public CustomerRelation getCustomerRelation(long fromCustomerId, long toCustomerId){
        LambdaQueryWrapper<CustomerRelation> query = new LambdaQueryWrapper<>();
        if (fromCustomerId > 0){
            query.eq(CustomerRelation::getFromCustomerId, fromCustomerId);
        }
        if (toCustomerId > 0){
            query.eq(CustomerRelation::getToCustomerId, toCustomerId);
        }
        return customerRelationMapper.selectOne(query);
    }
}
