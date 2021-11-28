package com.x.provider.mc.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.provider.api.mc.enums.MessageTargetType;
import com.x.provider.mc.enums.ChannelTypeEnum;
import com.x.provider.mc.model.domain.Message;
import com.x.provider.mc.service.CentrifugoEngineService;
import com.x.provider.mc.service.MessageEngineService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessageEngineServiceImpl implements MessageEngineService {

    private final CentrifugoEngineService centrifugoEngineService;

    public MessageEngineServiceImpl(CentrifugoEngineService imEngineService){
        this.centrifugoEngineService = imEngineService;
    }

    @Override
    public void sendMessage(String channel, Message message) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("id", message.getId());
        msg.put("senderUid", message.getSenderUid());
        msg.put("targetId", message.getTargetId());
        msg.put("messageType", message.getMessageType());
        msg.put("alertMsg", message.getAlertMsg());
        msg.put("msgBody", message.getMsgBody());
        msg.put("createdOnUtc", message.getCreatedOnUtc());
        this.centrifugoEngineService.publish(channel, msg);
    }

    @Override
    public void sendMessage(MessageTargetType messageTargetType, Message message) {
        sendMessage(getChannelName(message.getTargetId(),  messageTargetType), message);
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
    public String getChannelName(Long targetId, MessageTargetType messageTargetType){
        if (messageTargetType.getValue().equals(MessageTargetType.PERSONAL.getValue())){
            return StrUtil.format("personal:target_id#{}", targetId);
        }
        if (messageTargetType.getValue().equals(MessageTargetType.ALL.getValue())){
            return StrUtil.format("all:target_id_{}", targetId);
        }
        throw new IllegalStateException("not support message target type: " + messageTargetType.getValue());
    }
}
