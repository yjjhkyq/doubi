package com.x.provider.mc.component;

import com.x.provider.api.mc.constants.McEventTopic;
import com.x.provider.api.mc.model.event.MessageEvent;
import com.x.provider.mc.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    private final MessageService messageService;

    public KafkaConsumer(MessageService messageService){
        this.messageService = messageService;
    }

    @KafkaListener(topics = McEventTopic.TOPIC_NAME_SEND_MESSAGE)
    public void onSendMessage(MessageEvent event) {
        messageService.onSendMessage(event);
    }
}
