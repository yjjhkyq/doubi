package com.x.provider.statistic.component;

import com.x.provider.api.statistic.constants.StatisticEventTopic;
import com.x.provider.api.statistic.model.event.IncMetricValueEvent;
import com.x.provider.statistic.service.StatisticTotalService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final StatisticTotalService statisticTotalService;

    public KafkaConsumer(StatisticTotalService statisticTotalService){
        this.statisticTotalService = statisticTotalService;
    }

    @KafkaListener(topics = StatisticEventTopic.TOPIC_NAME_STAT_INC_METRIC_VALUE_EVENT)
    public void receive(IncMetricValueEvent event) {
        statisticTotalService.onStatTotal(event);
    }
}
