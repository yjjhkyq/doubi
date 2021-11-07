package com.x.kafka;

import com.x.kafka.constants.SystemEventTopic;
import com.x.kafka.enums.DelayTimeEnum;
import com.x.kafka.model.DelayMessage;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.Map;

/**
 * @author: liushenyi
 * @date: 2021/10/25/19:17
 */
public class KafkaService {

    private static final Map<DelayTimeEnum, String> DELAY_TOPIC_DELAY_TIME = Map.of(DelayTimeEnum.MINUTE_10, SystemEventTopic.SYSTEM_TOPIC_DELAY_10_MINUTE,
            DelayTimeEnum.MINUTE_20, SystemEventTopic.SYSTEM_TOPIC_DELAY_20_MINUTE, DelayTimeEnum.MINUTE_30, SystemEventTopic.SYSTEM_TOPIC_DELAY_30_MINUTE);

    public static String getDelayTopic(DelayTimeEnum delayTime){
        return DELAY_TOPIC_DELAY_TIME.get(delayTime);
    }

    public static long getDelayTimeInMillis(DelayTimeEnum delayTime){
        switch (delayTime){
            case MINUTE_10:
                return Duration.ofMinutes(10).getSeconds() * 1000;
            case MINUTE_20:
                return Duration.ofMinutes(20).getSeconds() * 1000;
            case MINUTE_30:
                return Duration.ofMinutes(30).getSeconds() * 1000;
            default:
                throw new IllegalArgumentException(delayTime.toString());
        }
    }

    public static void reSend(KafkaTemplate<String, Object> kafkaTemplate, DelayMessage message){
        while (System.currentTimeMillis() < message.getReSendTimeMillis()){
            try {
                Thread.sleep(1000L);
            }
            catch (Exception ee){

            }
        }
        kafkaTemplate.send(message.getTopic(), message.getKey(), message.getValue());
    }
}
