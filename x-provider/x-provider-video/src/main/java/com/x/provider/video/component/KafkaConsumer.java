package com.x.provider.video.component;

import com.x.core.utils.JsonUtil;
import com.x.provider.api.finance.model.event.FinanceDataChangedEvent;
import com.x.provider.api.finance.model.event.FinanceDataChangedEventEnum;
import com.x.provider.video.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    private final TopicService topicService;

    public KafkaConsumer(TopicService topicService){
        this.topicService = topicService;
    }

    @KafkaListener(topics = FinanceDataChangedEventEnum.TOPIC_NAME)
    public void receive(FinanceDataChangedEvent data) {
        topicService.onFinanceDataChanged(data);
    }
}
