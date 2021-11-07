package com.x.core.configure;

import com.x.core.cache.event.EntityChangedEventBus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: liushenyi
 * @date: 2021/11/02/14:40
 */
@Configuration
public class CacheEventConfiguration {
    @Bean
    @ConditionalOnMissingBean(EntityChangedEventBus.class)
    public EntityChangedEventBus eventBus(){
        return new EntityChangedEventBus();
    }
}
