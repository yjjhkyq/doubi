package com.x.provider.general.component;

import com.x.provider.api.general.constants.GeneralEventTopic;
import com.x.provider.api.general.model.event.StarRequestEvent;
import com.x.provider.general.service.StarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    private final StarService starService;

    public KafkaConsumer(StarService starService){
        this.starService = starService;
    }

    @KafkaListener(topics = GeneralEventTopic.TOPIC_NAME_STAR_REQUEST)
    public void onStarRequest(StarRequestEvent event) {
        starService.onStarRequest(event);
    }
}
