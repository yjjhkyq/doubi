package com.x.provider.mc.controller.admin;

import com.x.core.web.api.R;
import com.x.provider.mc.model.event.SendMessageEvent;
import com.x.provider.mc.service.WebSocketEngineService;
import com.x.provider.mc.service.impl.XWebSocketServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/mc")
public class McAdminController {

    private final XWebSocketServiceImpl webSocketEngineService;

    public McAdminController(XWebSocketServiceImpl webSocketEngineService){
        this.webSocketEngineService = webSocketEngineService;
    }

    @PostMapping("/ws/send/message")
    public R<Void> onFinanceDataChanged(SendMessageEvent sendMessageEvent){
        webSocketEngineService.sendMessage(sendMessageEvent.getToCustomerId(), sendMessageEvent.getToCustomerId(), sendMessageEvent.getMessage());
        return R.ok();
    }

}
