package com.paascloud.provider.customer.service.cache;

import com.google.common.eventbus.Subscribe;
import com.paascloud.core.cache.CacheEventConsumerHelper;
import com.paascloud.provider.customer.service.RedisKeyService;
import com.paascloud.provider.customer.service.cache.customer.CustomerChangedEvent;
import com.paascloud.provider.customer.service.cache.customer.CustomerPasswordChangedEvent;
import com.paascloud.provider.customer.service.cache.customer.CustomerRelationChangedEvent;
import com.paascloud.provider.customer.service.cache.customer.CustomerRoleChangedEvent;
import com.paascloud.redis.service.RedisService;
import org.springframework.stereotype.Component;

@Component
public class EventListener {

    public final RedisKeyService redisKeyService;
    private final RedisService redisService;

    public EventListener(RedisKeyService redisKeyService,
                         RedisService redisService){
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
    }

    @Subscribe
    public void onCustomerChangedEvent(CustomerChangedEvent event){
        CacheEventConsumerHelper.onEntityChangedEvent(event,
                t ->{
                },
                t -> {
                    redisService.deleteObject(redisKeyService.getCustomerKey(t.getUserName()));
                    redisService.deleteObject(redisKeyService.getCustomerKey(t.getId()));
                },
                t ->{
                    redisService.deleteObject(redisKeyService.getCustomerKey(t.getUserName()));
                    redisService.deleteObject(redisKeyService.getCustomerKey(t.getId()));
                    redisService.deleteObject(redisKeyService.getCustomerRoleKey(t.getId()));
                });
    }

    @Subscribe
    public void onCustomerPasswordChangedEvent(CustomerPasswordChangedEvent event){
        CacheEventConsumerHelper.onEntityChangedEvent(event,
                t ->{
                },
                t -> {
                    redisService.deleteObject(redisKeyService.getCustomerPasswordKey(t.getCustomerId()));
                },
                t ->{
                    redisService.deleteObject(redisKeyService.getCustomerPasswordKey(t.getCustomerId()));
                });
    }

    @Subscribe
    public void onCustomerRoleChangedEvent(CustomerRoleChangedEvent event){
        CacheEventConsumerHelper.onEntityChangedEvent(event,
                t ->{
                },
                t -> {
                    redisService.deleteObject(redisKeyService.getCustomerRoleKey(t.getCustomerId()));
                },
                t ->{
                    redisService.deleteObject(redisKeyService.getCustomerRoleKey(t.getCustomerId()));
                });
    }

    @Subscribe
    public void onCustomerRelationChangedEvent(CustomerRelationChangedEvent event){
        CacheEventConsumerHelper.onEntityChangedEvent(event,
                t ->{
                    redisService.setCacheObject(redisKeyService.getCustomerRelationKey(t.getFromCustomerId(), t.getToCustomerId()), t);
                },
                t -> {
                    redisService.setCacheObject(redisKeyService.getCustomerRelationKey(t.getFromCustomerId(), t.getToCustomerId()), t);
                },
                t ->{
                });
    }
}
