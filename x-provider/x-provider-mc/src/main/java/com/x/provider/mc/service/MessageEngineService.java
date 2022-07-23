package com.x.provider.mc.service;

import com.x.provider.api.mc.model.ao.SendMessageRawAO;
import com.x.provider.mc.model.dto.ConnectInfoDTO;
import com.x.provider.mc.model.dto.ConversationDTO;
import com.x.provider.mc.model.dto.MessageDTO;

import java.util.List;

public interface MessageEngineService {
    void sendMessage(ConversationDTO conversation, MessageDTO message);
    void sendMessage(SendMessageRawAO sendMessageRawAO);
    List<ConnectInfoDTO> listConnectionInfo(Long customerId);
}
