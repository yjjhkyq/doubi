package com.x.provider.customer.component;

import com.x.provider.api.customer.constants.CustomerEventTopic;
import com.x.provider.api.customer.model.event.CustomerInfoGreenEvent;
import com.x.provider.api.customer.model.event.FollowEvent;
import com.x.provider.customer.service.CustomerMcService;
import com.x.provider.customer.service.CustomerStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    private final CustomerMcService customerMcService;
    private final CustomerStatService customerStatService;

    public KafkaConsumer(CustomerMcService customerMcService,
                         CustomerStatService customerStatService){
        this.customerMcService = customerMcService;
        this.customerStatService = customerStatService;
    }

    @KafkaListener(topics = CustomerEventTopic.TOPIC_NAME_FOLLOW)
    public void onFollowEvent(FollowEvent event) {
        customerMcService.onFollowEvent(event);
        customerStatService.onFollowEvent(event);
    }

    @KafkaListener(topics = CustomerEventTopic.TOPIC_NAME_CUSTOMER_INFO_GREEN)
    public void onFollowEvent(CustomerInfoGreenEvent event) {
        customerMcService.onCustomerInfoGreen(event);
    }
}
