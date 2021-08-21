package com.paascloud.provider.vod.service.cache;

import com.paascloud.core.cache.event.EntityChangedEventBus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class EventHandler {

    private final EntityChangedEventBus eventBus;
    private final EventListener eventListener;

    public EventHandler(EntityChangedEventBus eventBus, EventListener eventListener){
        this.eventBus = eventBus;
        this.eventListener = eventListener;
    }

    @PostConstruct
    public void init() {
        eventBus.register(eventListener);
    }

    @PreDestroy
    public void destroy() {
        eventBus.unregister(eventListener);
    }


}
