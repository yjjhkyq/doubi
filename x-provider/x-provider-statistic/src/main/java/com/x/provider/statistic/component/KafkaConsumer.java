package com.x.provider.statistic.component;

import com.x.provider.api.statistic.constants.StatisticEventTopic;
import com.x.provider.api.statistic.model.event.StatisticTotalEvent;
import com.x.provider.statistic.service.StatisticTotalService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final StatisticTotalService statisticTotalService;

    public KafkaConsumer(StatisticTotalService statisticTotalService){
        this.statisticTotalService = statisticTotalService;
    }

    @KafkaListener(topics = StatisticEventTopic.TOPIC_NAME_STAT_TOTAL_EVENT)
    public void receive(StatisticTotalEvent event) {
        statisticTotalService.onStatTotal(event);
    }
}
