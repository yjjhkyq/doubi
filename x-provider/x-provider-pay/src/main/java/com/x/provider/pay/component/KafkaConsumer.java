package com.x.provider.pay.component;

import com.x.provider.api.customer.constants.CustomerEventTopic;
import com.x.provider.api.customer.model.event.CustomerEvent;
import com.x.provider.pay.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    private final PayService payService;

    public KafkaConsumer(PayService payService){
        this.payService = payService;
    }

    @KafkaListener(topics = CustomerEventTopic.TOPIC_NAME_CUSTOMER_INFO_CHANGED)
    public void onCustomerInfoChanged(CustomerEvent event) {
        if (CustomerEvent.EventTypeEnum.ADD.getValue().equals(event.getEventType())){
            payService.initAsset(event.getId());
        }
    }

}
