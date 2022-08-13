package com.x.provider.mc.service;

import com.x.provider.api.mc.model.dto.SendMessageRawDTO;
import com.x.provider.mc.model.bo.ConnectInfoBO;
import com.x.provider.mc.model.bo.ConversationBO;
import com.x.provider.mc.model.bo.MessageBO;

import java.util.List;

public interface MessageEngineService {
    void sendMessage(ConversationBO conversation, MessageBO message);
    void sendMessage(SendMessageRawDTO sendMessageRawAO);
    List<ConnectInfoBO> listConnectionInfo(Long customerId);
}
