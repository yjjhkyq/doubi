package com.x.provider.cms.component;

import com.x.provider.api.customer.constants.CustomerEventTopic;
import com.x.provider.api.customer.model.event.CustomerEvent;
import com.x.provider.api.finance.constants.FinanceEventTopic;
import com.x.provider.api.finance.model.event.SecurityChangedBatchEvent;
import com.x.provider.api.video.constants.VideoEventTopic;
import com.x.provider.api.video.model.event.TopicBatchEvent;
import com.x.provider.api.video.model.event.VideoChangedEvent;
import com.x.provider.cms.controller.frontend.SearchController;
import com.x.provider.cms.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    private final SearchService searchService;

    public KafkaConsumer(SearchService searchService){
        this.searchService = searchService;
    }

    @KafkaListener(topics = FinanceEventTopic.TOPIC_NAME_SECURITY_BATCH_CHANGED)
    public void onStarRequest(SecurityChangedBatchEvent event) {
        searchService.onSecurityChanged(event);
    }

    @KafkaListener(topics = CustomerEventTopic.TOPIC_NAME_CUSTOMER_INFO_CHANGED)
    public void onCustomerEvent(CustomerEvent event) {
        searchService.onCustomerInfoChanged(event);
    }

    @KafkaListener(topics = VideoEventTopic.TOPIC_NAME_VIDEO_CHANGED)
    public void onCustomerEvent(VideoChangedEvent event) {
        searchService.onVideoChanged(event);
    }

    @KafkaListener(topics = VideoEventTopic.TOPIC_NAME_TOPIC_CHANGED_BATCH)
    public void onTopicBatchEvent(TopicBatchEvent event) {
        searchService.onTopicBatchChangedEvent(event);
    }
}
