package com.x.provider.mc.controller.rpc;

import com.x.core.web.api.R;
import com.x.provider.api.mc.model.dto.SendMessageRequestDTO;
import com.x.provider.api.mc.model.dto.SendMessageRawDTO;
import com.x.provider.api.mc.service.MessageRpcService;
import com.x.provider.mc.service.MessageEngineService;
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
    private final MessageEngineService messageEngineService;

    public MessageRpcController(MessageService messageService,
                                MessageEngineService messageEngineService){
        this.messageService = messageService;
        this.messageEngineService = messageEngineService;
    }

    @PostMapping("send")
    @Override
    public R<Long> sendMessage(@RequestBody SendMessageRequestDTO sendMessageAO) {
        return R.ok(messageService.sendMessage(sendMessageAO).getId());
    }

    @PostMapping("send/raw")
    @Override
    public R<Long> sendMessageRaw(@RequestBody SendMessageRawDTO sendMessageAO) {
        messageEngineService.sendMessage(sendMessageAO);
        return R.ok();
    }
}
