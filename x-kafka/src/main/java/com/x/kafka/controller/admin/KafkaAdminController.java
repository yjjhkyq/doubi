package com.x.kafka.controller.admin;

import com.x.core.utils.JsonUtil;
import com.x.core.web.api.R;
import com.x.kafka.DelayKafkaTemplate;
import com.x.kafka.enums.DelayTimeEnum;
import com.x.kafka.model.event.TestDelayMessageEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/kafka")
public class KafkaAdminController {

    private final DelayKafkaTemplate delayKafkaTemplate;

    public KafkaAdminController(DelayKafkaTemplate delayKafkaTemplate){
        this.delayKafkaTemplate = delayKafkaTemplate;
    }

    @PostMapping("/on/total/event")
    public R<Void> testSendDelayMsg(){
        TestDelayMessageEvent testDelayMessageEvent = new TestDelayMessageEvent();
        testDelayMessageEvent.setDelayMessage("test delay message of 10 minute");
        delayKafkaTemplate.sendDelayMessage("sys-delay-test", "sdf", JsonUtil.toJSONString(testDelayMessageEvent), DelayTimeEnum.MINUTE_10);
        return R.ok();
    }
}
