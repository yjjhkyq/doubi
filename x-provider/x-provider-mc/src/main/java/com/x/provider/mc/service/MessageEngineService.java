package com.x.provider.mc.service;

import com.x.provider.api.mc.enums.MessageTargetType;
import com.x.provider.mc.enums.ChannelTypeEnum;
import com.x.provider.mc.model.domain.Message;

public interface MessageEngineService {
    void sendMessage(String channel, Message message);
    void sendMessage(MessageTargetType messageTargetType, Message message);
    String authenticationToken(Long customerId);
    String getWebSocketUrl();
    String getChannelName(Long targetId, MessageTargetType messageTargetType);
}
