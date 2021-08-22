package com.x.core.configure;

import com.x.core.cache.event.EntityChangedEventBus;
import com.x.core.web.api.XResponseEntityDecoder;
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
public class XFeignAutoConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

      @Bean
     @ConditionalOnMissingBean
     public Decoder feignDecoder() {
          return new OptionalDecoder(new XResponseEntityDecoder(new SpringDecoder(this.messageConverters)));
      }

    @Bean
    @ConditionalOnMissingBean(EntityChangedEventBus.class)
    public EntityChangedEventBus eventBus(){
        return new EntityChangedEventBus();
    }

}
