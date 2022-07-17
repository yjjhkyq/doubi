package com.x.provider.mc.service;

import com.x.provider.api.mc.model.ao.SendMessageRawAO;
import com.x.provider.mc.model.domain.Message;
import com.x.provider.mc.model.dto.ConversationDTO;
import com.x.provider.mc.model.dto.MessageDTO;

public interface MessageEngineService {
    void sendMessage(ConversationDTO conversation, MessageDTO message);
    String authenticationToken(Long customerId);
    String getWebSocketUrl();
    String getChannelName(String targetId, Integer messageTargetType);
    String getChannelName(Long groupId, Long customerId);
    void sendMessage(SendMessageRawAO sendMessageRawAO);
}
