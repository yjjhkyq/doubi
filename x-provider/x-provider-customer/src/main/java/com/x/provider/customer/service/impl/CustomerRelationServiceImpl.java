package com.x.provider.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.cache.event.EntityChangedEventBus;
import com.x.provider.api.customer.constants.CustomerEventTopic;
import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.api.customer.model.event.FollowEvent;
import com.x.provider.customer.mapper.CustomerRelationMapper;
import com.x.provider.customer.model.domain.CustomerRelation;
import com.x.provider.customer.service.CustomerRelationService;
import com.x.provider.customer.service.RedisKeyService;
import com.x.provider.customer.service.cache.customer.CustomerRelationChangedEvent;
import com.x.redis.service.RedisService;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public CustomerRelationServiceImpl(RedisService redisService,
                                       RedisKeyService redisKeyService,
                                       CustomerRelationMapper customerRelationMapper,
                                       EntityChangedEventBus entityChangedEventBus,
                                       KafkaTemplate<String, Object> kafkaTemplate){
        this.redisService = redisService;
        this.redisKeyService = redisKeyService;
        this.customerRelationMapper = customerRelationMapper;
        this.entityChangedEventBus = entityChangedEventBus;
        this.kafkaTemplate = kafkaTemplate;
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
        changeCustomerRelation(fromCustomerId, toCustomerId, true);

    }

    @Override
    @Transactional
    public void unFollowing(long fromCustomerId, long toCustomerId) {
        changeCustomerRelation(fromCustomerId, toCustomerId, false);
    }

    private void changeCustomerRelation(long fromCustomerId, long toCustomerId, boolean follow){
        CustomerRelation customerRelationFrom = getCustomerRelation(fromCustomerId, toCustomerId);
        CustomerRelation customerRelationTo = getCustomerRelation(toCustomerId, fromCustomerId);
        CustomerRelationEnum newRelationFrom = null;
        CustomerRelationEnum newRelationTo = null;
        if (follow){
            newRelationFrom =  customerRelationTo != null && customerRelationTo.getRelation() == CustomerRelationEnum.FOLLOW.getValue() ? CustomerRelationEnum.FRIEND : CustomerRelationEnum.FOLLOW;
            newRelationTo = newRelationFrom.getValue() == CustomerRelationEnum.FRIEND.getValue() ? CustomerRelationEnum.FRIEND : null;
        }
        else{
            newRelationFrom = customerRelationFrom != null && customerRelationFrom.getRelation() == CustomerRelationEnum.NO_RELATION.getValue() ? null : CustomerRelationEnum.NO_RELATION;
            newRelationTo = customerRelationTo != null && customerRelationTo.getRelation() == CustomerRelationEnum.FRIEND.getValue() ? CustomerRelationEnum.FOLLOW : null;
        }
        if (newRelationFrom != null){
            if (newRelationFrom.getValue() != CustomerRelationEnum.NO_RELATION.getValue()) {
                redisService.zadd(redisKeyService.getCustomerRelationOfFollowKey(fromCustomerId), toCustomerId, System.currentTimeMillis());
                redisService.zadd(redisKeyService.getCustomerRelationOfFansKey(toCustomerId), fromCustomerId, System.currentTimeMillis());
            } else {
                redisService.zremove(redisKeyService.getCustomerRelationOfFollowKey(fromCustomerId), toCustomerId);
                redisService.zremove(redisKeyService.getCustomerRelationOfFansKey(toCustomerId), fromCustomerId);
            }
            changeCustomerRelation(customerRelationFrom, CustomerRelation.builder().fromCustomerId(fromCustomerId).toCustomerId(toCustomerId).relation(newRelationFrom.getValue()).build());
            if (follow){
                kafkaTemplate.send(CustomerEventTopic.TOPIC_NAME_FOLLOW, String.valueOf(fromCustomerId),
                        FollowEvent.builder()
                                .firstFollow(customerRelationFrom == null)
                                .fromCustomerId(fromCustomerId)
                                .toCustomerId(toCustomerId)
                                .build());
            }
        }
        if (newRelationTo != null){
            changeCustomerRelation(customerRelationTo, CustomerRelation.builder().fromCustomerId(toCustomerId).toCustomerId(fromCustomerId).relation(newRelationTo.getValue()).build());
        }
    }

    void changeCustomerRelation(CustomerRelation customerRelationOld, CustomerRelation customerRelationNew){
        if (customerRelationOld == null){
            customerRelationMapper.insert(customerRelationNew);
            entityChangedEventBus.postEntityInserted(new CustomerRelationChangedEvent(customerRelationNew));
        }
        else {
            customerRelationNew.setId(customerRelationOld.getId());
            customerRelationMapper.updateById(customerRelationNew);
            entityChangedEventBus.postEntityUpdated(new CustomerRelationChangedEvent(customerRelationNew));
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
