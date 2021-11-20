package com.x.provider.mc.controller.rpc;

import com.x.core.web.api.R;
import com.x.provider.api.mc.model.ao.SendNotifyAO;
import com.x.provider.api.mc.service.NotifyRpcService;
import com.x.provider.mc.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rpc/notify")
public class NotifyRpcController implements NotifyRpcService {

    private final NotifyService notifyService;

    public NotifyRpcController(NotifyService notifyService){
        this.notifyService = notifyService;
    }

    @PostMapping("send")
    @Override
    public R<Void> sendNotify(@RequestBody SendNotifyAO sendNotifyAO) {
        notifyService.sendNotify(sendNotifyAO);
        return R.ok();
    }
}
