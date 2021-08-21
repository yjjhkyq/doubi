package com.paascloud.core.configure;

import com.google.common.eventbus.EventBus;
import com.paascloud.core.cache.event.EntityChangedEventBus;
import com.paascloud.core.web.api.PaascloudResponseEntityDecoder;
import feign.Feign;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({Feign.class})
public class PaascloudFeignAutoConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

      @Bean
     @ConditionalOnMissingBean
     public Decoder feignDecoder() {
          return new OptionalDecoder(new PaascloudResponseEntityDecoder(new SpringDecoder(this.messageConverters)));
      }

    @Bean
    @ConditionalOnMissingBean(EntityChangedEventBus.class)
    public EntityChangedEventBus eventBus(){
        return new EntityChangedEventBus();
    }

}
