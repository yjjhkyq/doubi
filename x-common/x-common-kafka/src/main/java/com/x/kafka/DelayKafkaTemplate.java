package com.x.kafka;

import com.x.kafka.enums.DelayTimeEnum;
import com.x.kafka.model.DelayMessage;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author: liushenyi
 * @date: 2021/10/25/18:55
 */
public class DelayKafkaTemplate {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DelayKafkaTemplate(KafkaTemplate<String, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDelayMessage(String topic, String key, String value, DelayTimeEnum delayTime){
        kafkaTemplate.send(KafkaService.getDelayTopic(delayTime), key, new DelayMessage(topic, key, value, KafkaService.getDelayTimeInMillis(delayTime)));
    }
}
