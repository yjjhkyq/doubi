package com.x.provider.mc.controller.rpc;

import com.x.core.web.api.R;
import com.x.provider.api.mc.model.ao.SendMessageAO;
import com.x.provider.api.mc.service.MessageRpcService;
import com.x.provider.mc.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rpc/message")
public class MessageRpcController implements MessageRpcService {

    private final MessageService messageService;

    public MessageRpcController(MessageService messageService){
        this.messageService = messageService;
    }

    @PostMapping("send")
    @Override
    public R<Void> sendMessage(@RequestBody SendMessageAO sendMessageAO) {
        messageService.sendMessage(sendMessageAO);
        return R.ok();
    }
}
