package com.x.provider.mc.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.core.utils.CompareUtils;
import com.x.core.utils.JsonUtil;
import com.x.provider.api.mc.enums.ConversationType;
import com.x.provider.api.mc.model.ao.SendMessageRawAO;
import com.x.provider.mc.model.domain.Message;
import com.x.provider.mc.model.dto.ConversationDTO;
import com.x.provider.mc.model.dto.MessageDTO;
import com.x.provider.mc.service.CentrifugoEngineService;
import com.x.provider.mc.service.MessageEngineService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class MessageEngineServiceImpl implements MessageEngineService {

    private static final String CHANNEL_FORMATTER = "{}_{}";

    private final CentrifugoEngineService centrifugoEngineService;

    public MessageEngineServiceImpl(CentrifugoEngineService imEngineService){
        this.centrifugoEngineService = imEngineService;
    }

    @Override
    public void sendMessage(ConversationDTO conversation, MessageDTO message) {
        Map<String, Object> data = Map.of("conversation", conversation, "message", message);
        SendMessageRawAO sendMessageRawAO = SendMessageRawAO.builder()
                .jsonData(JsonUtil.toJSONString(data))
                .messageClass(message.getMessageClass())
                .messageType(message.getMessageType())
                .toCustomerId(message.getToCustomerId())
                .toGroupId(message.getToGroupId())
                .build();
        sendMessage(sendMessageRawAO);
    }

    @Override
    public void sendMessage(SendMessageRawAO sendMessageRawAO){
        Map<String, Object> msg = new HashMap<>();
        msg.put("messageClass", sendMessageRawAO.getMessageClass());
        msg.put("messageType", sendMessageRawAO.getMessageType());
        msg.put("jsonData", sendMessageRawAO.getJsonData());
        this.centrifugoEngineService.publish(getChannelName(sendMessageRawAO.getToCustomerId(), sendMessageRawAO.getToGroupId()), msg);
    }

    @Override
    public String authenticationToken(Long customerId) {
        return centrifugoEngineService.authenticationToken(customerId.toString(), 0);
    }

    @Override
    public String getWebSocketUrl() {
        return centrifugoEngineService.getWebSocketUrl();
    }

    @Override
    public String getChannelName(String targetId, Integer conversationType){
        return StrUtil.format("{}_{}", ConversationType.valueOf(conversationType).name(), targetId);
    }

    @Override
    public String getChannelName(Long customerId, Long groupId) {
        if (CompareUtils.gtZero(customerId)){
            return getChannelName(customerId.toString(), ConversationType.C2C.getValue());
        }
        return getChannelName(groupId.toString(), ConversationType.GROUP.getValue());
    }
}
