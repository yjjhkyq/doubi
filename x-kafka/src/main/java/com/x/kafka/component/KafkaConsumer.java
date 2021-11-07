package com.x.kafka.component;

import com.x.core.utils.JsonUtil;
import com.x.kafka.KafkaService;
import com.x.kafka.constants.SystemEventTopic;
import com.x.kafka.model.DelayMessage;
import com.x.kafka.model.event.TestDelayMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaConsumer(KafkaTemplate<String, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = SystemEventTopic.SYSTEM_TOPIC_DELAY_10_MINUTE)
    public void receive10Minute(DelayMessage delayMessage) {
        KafkaService.reSend(kafkaTemplate, delayMessage);
    }

    @KafkaListener(topics = SystemEventTopic.SYSTEM_TOPIC_DELAY_20_MINUTE)
    public void receive20Minute(DelayMessage delayMessage) {
        KafkaService.reSend(kafkaTemplate, delayMessage);
    }

    @KafkaListener(topics = SystemEventTopic.SYSTEM_TOPIC_DELAY_30_MINUTE)
    public void receive30Minute(DelayMessage delayMessage) {
        KafkaService.reSend(kafkaTemplate, delayMessage);
    }

    @KafkaListener(topics = "sys-delay-test")
    public void testConsumeDelayMsg(String eventValue) {
        TestDelayMessageEvent testDelayMessageEvent = JsonUtil.parseObject(eventValue, TestDelayMessageEvent.class);
        log.info("delay msg receive, msg:{}", testDelayMessageEvent.getDelayMessage());
    }
}
