package com.x.kafka.configure;

import com.x.kafka.DelayKafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author: liushenyi
 * @date: 2021/10/26/17:38
 */
@Configuration
public class KafkaConfig {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Bean("delayKafkaTemplate")
    public DelayKafkaTemplate delayKafkaTemplate(){
        return new DelayKafkaTemplate(kafkaTemplate);
    }
}
