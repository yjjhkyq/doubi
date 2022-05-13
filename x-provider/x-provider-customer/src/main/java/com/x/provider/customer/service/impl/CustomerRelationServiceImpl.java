package com.x.provider.customer.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.cache.event.EntityChangedEventBus;
import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
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
import org.springframework.util.CollectionUtils;

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
        if (newRelationTo != null){
            changeCustomerRelation(customerRelationTo, CustomerRelation.builder().fromCustomerId(toCustomerId).toCustomerId(fromCustomerId).relation(newRelationTo.getValue()).build());
        }
        if (newRelationFrom != null){
            long customerRelationId = changeCustomerRelation(customerRelationFrom, CustomerRelation.builder().fromCustomerId(fromCustomerId).toCustomerId(toCustomerId).relation(newRelationFrom.getValue()).build());
            if (newRelationFrom.getValue() != CustomerRelationEnum.NO_RELATION.getValue()) {
                redisService.zadd(redisKeyService.getCustomerRelationOfFollowKey(fromCustomerId), toCustomerId, customerRelationId);
                redisService.zadd(redisKeyService.getCustomerRelationOfFansKey(toCustomerId), fromCustomerId, customerRelationId);
            } else {
                redisService.zremove(redisKeyService.getCustomerRelationOfFollowKey(fromCustomerId), toCustomerId);
                redisService.zremove(redisKeyService.getCustomerRelationOfFansKey(toCustomerId), fromCustomerId);
            }
            if (follow){
                kafkaTemplate.send(CustomerEventTopic.TOPIC_NAME_FOLLOW, String.valueOf(fromCustomerId),
                        FollowEvent.builder()
                                .eventType(follow ? FollowEvent.EventTypeEnum.FOLLOW.getValue() : FollowEvent.EventTypeEnum.UN_FOLLOW.getValue())
                                .firstFollow(customerRelationFrom == null)
                                .fromCustomerId(fromCustomerId)
                                .toCustomerId(toCustomerId)
                                .build());
            }
        }
    }

    private long changeCustomerRelation(CustomerRelation customerRelationOld, CustomerRelation customerRelationNew){
        prepare(customerRelationNew);
        if (customerRelationOld == null){
            customerRelationMapper.insert(customerRelationNew);
            entityChangedEventBus.postEntityInserted(new CustomerRelationChangedEvent(customerRelationNew));
            return customerRelationNew.getId();
        }
        else {
            customerRelationNew.setId(customerRelationOld.getId());
            customerRelationMapper.updateById(customerRelationNew);
            entityChangedEventBus.postEntityUpdated(new CustomerRelationChangedEvent(customerRelationNew));
            return customerRelationNew.getId();
        }
    }

    @Override
    public PageList<CustomerRelation> listFollow(long customerId, PageDomain page) {
        return listFollowFans(customerId, 0L, page);
    }

    @Override
    public List<Long> listFollow(long customerId) {
        return redisService.rangeLong((redisKeyService.getCustomerRelationOfFollowKey(customerId))).stream().collect(Collectors.toList());
    }

    @Override
    public PageList<CustomerRelation> listFans(long customerId, PageDomain page) {
        LambdaQueryWrapper<CustomerRelation> query = buildQuery(0, customerId, page);
        return listFollowFans(0L, customerId, page);
    }

    @Override
    public long getFansCount(long customerId) {
        return redisService.zsize(redisKeyService.getCustomerRelationOfFansKey(customerId));
    }

    @Override
    public long getFollowCount(long customerId) {
        return redisService.zsize(redisKeyService.getCustomerRelationOfFollowKey(customerId));
    }

    @Override
    public Map<Long, CustomerRelation> listRelationMap(long customerId, CustomerRelationEnum customerRelation, List<Long> toCustomerIdList) {
        List<CustomerRelation> customerRelations = listCustomerRelation(customerId, customerRelation, toCustomerIdList);
        switch (customerRelation){
            case FOLLOW:
                return customerRelations.stream().collect(Collectors.toMap(CustomerRelation::getToCustomerId, item -> item));
            case FANS:
                return customerRelations.stream().collect(Collectors.toMap(CustomerRelation::getFromCustomerId, item -> item));
            default:
                return Collections.emptyMap();
        }
    }

    private PageList<CustomerRelation> listFollowFans(long fromCustomerId, long toCustomerId, PageDomain page) {
        LambdaQueryWrapper<CustomerRelation> query = buildQuery(fromCustomerId, toCustomerId, page);
        List<CustomerRelation> customerRelations = customerRelationMapper.selectList(query);
        if (customerRelations.isEmpty()){
            return new PageList<>();
        }
        return new PageList<>(customerRelations, page.getPageSize(), CollectionUtils.lastElement(customerRelations).getId());
    }

    private List<CustomerRelation> listCustomerRelation(long customerId, CustomerRelationEnum customerRelation, List<Long> toCustomerIdList) {
        long fromCustomerId = customerRelation.getValue() == CustomerRelationEnum.FOLLOW.getValue() ? customerId : 0L;
        long toCustomerId = customerRelation.getValue() == CustomerRelationEnum.FANS.getValue() ? customerId : 0L;
        LambdaQueryWrapper<CustomerRelation> query = buildQuery(fromCustomerId, toCustomerId, toCustomerIdList, false);
        return customerRelationMapper.selectList(query);
    }


    @Override
    public Set<Long> listFollowCustomer(long fromCustomerId, List<Long> toCustomerIdList){
        return listCustomerRelation(fromCustomerId, CustomerRelationEnum.FOLLOW, toCustomerIdList).stream().map(CustomerRelation::getToCustomerId).collect(Collectors.toSet());
    }

    public Optional<CustomerRelation> getCacheCustomerRelation(long fromCustomerId, long toCustomerId){
        CustomerRelation customerRelation = getCustomerRelation(fromCustomerId, toCustomerId);
        return Optional.ofNullable(customerRelation);
    }

    public CustomerRelation getCustomerRelation(long fromCustomerId, long toCustomerId){
        LambdaQueryWrapper<CustomerRelation> query = buildQuery(fromCustomerId, toCustomerId, null, null);
        return customerRelationMapper.selectOne(query);
    }

    private LambdaQueryWrapper<CustomerRelation> buildQuery(long fromCustomerId, long toCustomerId, PageDomain page){
        LambdaQueryWrapper<CustomerRelation> result = buildQuery(fromCustomerId, toCustomerId, null, false)
                .orderByDesc(CustomerRelation::getId).last(StrUtil.format(" limit {}", page.getPageSize()));
        if (page.getCursor() != 0){
            result.lt(CustomerRelation::getId, page.getCursor());
        }
        return result;
    }

    private LambdaQueryWrapper<CustomerRelation> buildQuery(long fromCustomerId, long toCustomerId, List<Long> toCustomerIdList, Boolean delete) {
        LambdaQueryWrapper<CustomerRelation> query = new LambdaQueryWrapper<>();
        if (fromCustomerId > 0){
            query.eq(CustomerRelation::getFromCustomerId, fromCustomerId);
        }
        if (toCustomerId > 0){
            query.eq(CustomerRelation::getToCustomerId, toCustomerId);
        }
        if (!CollectionUtils.isEmpty(toCustomerIdList)){
            query.in(CustomerRelation::getToCustomerId, toCustomerIdList);
        }
        if (delete != null){
            query.eq(CustomerRelation::getDeleted, delete);
        }
        return query;
    }

    private void prepare(CustomerRelation customerRelation){
        customerRelation.setDeleted(CustomerRelationEnum.NO_RELATION.getValue() == customerRelation.getRelation());
    }
}
