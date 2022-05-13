package com.x.provider.video.controller.admin;

import com.x.core.web.api.R;
import com.x.core.web.controller.BaseAdminController;
import com.x.provider.api.finance.model.event.FinanceDataChangedEvent;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.video.component.KafkaConsumer;
import com.x.provider.video.service.TopicService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/mock")
public class MockAdminController extends BaseAdminController {

    private final KafkaConsumer kafkaConsumer;

    public MockAdminController(KafkaConsumer kafkaConsumer){
        this.kafkaConsumer = kafkaConsumer;
    }

    @PostMapping("/star")
    public R<Void> onFinanceDataChanged(@RequestBody StarEvent starEvent){
        kafkaConsumer.onStar(starEvent);
        return R.ok();
    }
}
