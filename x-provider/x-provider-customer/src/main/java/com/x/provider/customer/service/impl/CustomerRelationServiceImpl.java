package com.x.provider.customer.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.api.customer.constants.CustomerEventTopic;
import com.x.provider.api.customer.enums.CustomerRelationEnum;
import com.x.provider.api.customer.model.event.FollowEvent;
import com.x.provider.customer.mapper.CustomerRelationMapper;
import com.x.provider.customer.model.domain.CustomerRelation;
import com.x.provider.customer.model.query.ListCustomerRelationQuery;
import com.x.provider.customer.service.CustomerRelationService;
import com.x.provider.customer.service.RedisKeyService;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public CustomerRelationServiceImpl(RedisService redisService,
                                       RedisKeyService redisKeyService,
                                       CustomerRelationMapper customerRelationMapper,
                                       KafkaTemplate<String, Object> kafkaTemplate){
        this.redisService = redisService;
        this.redisKeyService = redisKeyService;
        this.customerRelationMapper = customerRelationMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public CustomerRelation getRelation(Long fromCustomerId, Long toCustomerId) {
        if(fromCustomerId == 0){
            return null;
        }
        LambdaQueryWrapper<CustomerRelation> query = buildQuery(ListCustomerRelationQuery.builder().fromCustomerId(fromCustomerId).toCustomerId(toCustomerId).build());
        return customerRelationMapper.selectOne(query);
    }

    @Override
    @Transactional
    public void following(Long fromCustomerId, Long toCustomerId) {
        changeCustomerRelation(fromCustomerId, toCustomerId, true);
    }

    @Override
    @Transactional
    public void unFollowing(Long fromCustomerId, Long toCustomerId) {
        changeCustomerRelation(fromCustomerId, toCustomerId, false);
    }

    private void changeCustomerRelation(Long fromCustomerId, Long toCustomerId, boolean follow){
        CustomerRelation customerRelationFrom = getRelation(fromCustomerId, toCustomerId);
        CustomerRelation customerRelationTo = getRelation(toCustomerId, fromCustomerId);
        if (customerRelationFrom == null){
            customerRelationFrom = CustomerRelation.builder().fromCustomerId(fromCustomerId).toCustomerId(toCustomerId).follow(follow).id(null).build();
        }
        customerRelationFrom.setFollow(follow);
        boolean isFriend = follow && customerRelationTo != null && customerRelationTo.getFollow() ? true : false;
        customerRelationFrom.setFriend(isFriend);
        if(customerRelationFrom.getId() == null) {
            customerRelationMapper.insert(customerRelationFrom);
        }
        else {
            customerRelationMapper.updateById(customerRelationFrom);
        }
        if (customerRelationTo != null){
            customerRelationTo.setFriend(isFriend);
            customerRelationMapper.updateById(customerRelationTo);
        }

        if (follow){
            kafkaTemplate.send(CustomerEventTopic.TOPIC_NAME_FOLLOW, String.valueOf(fromCustomerId),
                    FollowEvent.builder()
                            .eventType(follow ? FollowEvent.EventTypeEnum.FOLLOW.getValue() : FollowEvent.EventTypeEnum.UN_FOLLOW.getValue())
                            .firstFollow(customerRelationFrom == null)
                            .fromCustomerId(fromCustomerId)
                            .toCustomerId(toCustomerId)
                            .follow(customerRelationFrom.getFollow())
                            .friend(customerRelationFrom.getFriend())
                            .id(customerRelationFrom.getId())
                            .build());
        }
    }

    @Override
    public PageList<CustomerRelation> listFollow(Long customerId, PageDomain page) {
        return listCustomerRelationPage(ListCustomerRelationQuery.builder().fromCustomerId(customerId).follow(true).build(), page);
    }

    @Override
    public List<Long> listFollow(Long customerId) {
        return customerRelationMapper.selectList(buildQuery(ListCustomerRelationQuery.builder().fromCustomerId(customerId).build())).stream().map(CustomerRelation::getToCustomerId).collect(Collectors.toList());
    }

    @Override
    public PageList<CustomerRelation> listFans(Long customerId, PageDomain page) {
        return listCustomerRelationPage(ListCustomerRelationQuery.builder().toCustomerId(customerId).follow(true).build(), page);
    }

    @Override
    public Map<Long, CustomerRelation> listRelationMap(Long customerId, List<Long> customerIdList, CustomerRelationEnum customerRelation) {
        List<CustomerRelation> customerRelations = listCustomerRelation(customerId, customerIdList, customerRelation);
        switch (customerRelation){
            case FOLLOW:
                return customerRelations.stream().collect(Collectors.toMap(CustomerRelation::getToCustomerId, item -> item));
            case FANS:
                return customerRelations.stream().collect(Collectors.toMap(CustomerRelation::getFromCustomerId, item -> item));
            default:
                return Collections.emptyMap();
        }
    }

    private PageList<CustomerRelation> listCustomerRelationPage(ListCustomerRelationQuery listCustomerRelationQuery, PageDomain page) {
        LambdaQueryWrapper<CustomerRelation> query = buildPageQuery(listCustomerRelationQuery, page);
        List<CustomerRelation> customerRelations = customerRelationMapper.selectList(query);
        if (customerRelations.isEmpty()){
            return new PageList<>();
        }
        return new PageList<>(customerRelations, page.getPageSize(), CollectionUtils.lastElement(customerRelations).getId());
    }

    @Override
    public Set<Long> listFollowCustomer(Long fromCustomerId, List<Long> toCustomerIdList){
        return listCustomerRelation(fromCustomerId, toCustomerIdList, CustomerRelationEnum.FOLLOW).stream().map(CustomerRelation::getToCustomerId).collect(Collectors.toSet());
    }

    @Override
    public PageList<CustomerRelation> listCustomerRelation(Long customerId, CustomerRelationEnum customerRelation, PageDomain page) {
        final ListCustomerRelationQuery query = ListCustomerRelationQuery.builder().build();
        switch (customerRelation){
            case FRIEND:
                query.setFriend(true);
                query.setFromCustomerId(customerId);
                break;
            case FANS:
                query.setFollow(true);
                query.setToCustomerId(customerId);
                break;
            case FOLLOW:
                query.setFollow(true);
                query.setFromCustomerId(customerId);
                break;
            case NO_RELATION:
                throw new IllegalArgumentException(String.valueOf(customerRelation.getValue()));
        }
        return listCustomerRelationPage(query, page);
    }

    public List<CustomerRelation> listCustomerRelation(Long customerId, List<Long> customerIdList, CustomerRelationEnum customerRelation){
        ListCustomerRelationQuery query = new ListCustomerRelationQuery();
        switch (customerRelation){
            case FANS:
                query.setToCustomerId(customerId);
                query.setFromCustomerIdList(customerIdList);
            case FOLLOW:
                query.setFromCustomerId(customerId);
                query.setToCustomerIdList(customerIdList);
            case FRIEND:
                query.setFromCustomerId(customerId);
                query.setToCustomerIdList(customerIdList);
                query.setFriend(true);
            default:
        }
        return customerRelationMapper.selectList(buildQuery(query));
    }
    private LambdaQueryWrapper<CustomerRelation> buildPageQuery(ListCustomerRelationQuery customerRelationQuery, PageDomain page){
        LambdaQueryWrapper<CustomerRelation> result = buildQuery(customerRelationQuery)
                .orderByDesc(CustomerRelation::getId).last(StrUtil.format(" limit {}", page.getPageSize()));
        if (page.getCursor() != 0){
            result.lt(CustomerRelation::getId, page.getCursor());
        }
        return result;
    }

    private LambdaQueryWrapper<CustomerRelation> buildQuery(ListCustomerRelationQuery listCustomerRelationQuery) {
        LambdaQueryWrapper<CustomerRelation> query = new LambdaQueryWrapper<>();
        if (listCustomerRelationQuery.getFromCustomerId() != null){
            query.eq(CustomerRelation::getFromCustomerId, listCustomerRelationQuery.getFromCustomerId());
        }
        if (listCustomerRelationQuery.getToCustomerId() != null){
            query.eq(CustomerRelation::getToCustomerId, listCustomerRelationQuery.getToCustomerId());
        }
        if (!CollectionUtils.isEmpty(listCustomerRelationQuery.getToCustomerIdList())){
            query.in(CustomerRelation::getToCustomerId, listCustomerRelationQuery.getToCustomerIdList());
        }
        if (!CollectionUtils.isEmpty(listCustomerRelationQuery.getFromCustomerIdList())){
            query.in(CustomerRelation::getFromCustomerId, listCustomerRelationQuery.getFromCustomerIdList());
        }
        if (listCustomerRelationQuery.getFollow() != null){
            query.eq(CustomerRelation::getFollow, listCustomerRelationQuery.getFollow());
        }
        if (listCustomerRelationQuery.getFriend() != null){
            query.eq(CustomerRelation::getFriend, listCustomerRelationQuery.getFriend());
        }
        return query;
    }
}
