package com.x.provider.video.controller.admin;

import com.x.core.web.api.R;
import com.x.core.web.controller.BaseAdminController;
import com.x.provider.api.finance.model.event.FinanceDataChangedEvent;
import com.x.provider.video.service.TopicService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/topic")
public class TopicAdminController extends BaseAdminController {

    private final TopicService topicService;

    public TopicAdminController(TopicService topicService){
        this.topicService = topicService;
    }

    @PostMapping("/init")
    public R<Void> initTopic(){
        topicService.initTopic();
        return R.ok();
    }

    @PostMapping("/finance/data/changed")
    public R<Void> onFinanceDataChanged(@RequestBody FinanceDataChangedEvent financeDataChangedEvent){
        topicService.onFinanceDataChanged(financeDataChangedEvent);
        return R.ok();
    }
}
