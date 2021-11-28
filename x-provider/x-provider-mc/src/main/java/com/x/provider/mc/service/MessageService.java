package com.x.provider.mc.service;

import com.x.core.web.page.CursorList;
import com.x.core.web.page.CursorPageRequest;
import com.x.provider.api.mc.model.ao.SendMessageAO;
import com.x.provider.mc.model.ao.ReadMessageAO;
import com.x.provider.mc.model.domain.Message;
import com.x.provider.mc.model.domain.MessageReadBadge;
import com.x.provider.mc.model.domain.MessageSenderSystem;

import java.util.List;
import java.util.Set;

public interface MessageService {
    List<MessageSenderSystem> listMessageSenderSystem();
    CursorList<Message> readMessage(ReadMessageAO readMessageAO, Long customerId);
    void sendMessage(SendMessageAO notify);
    List<MessageReadBadge> listMessageReadBadge(Long customerId);

    String getWebSocketUrl();
    String authenticationToken(Long customerId);
    Set<String> subscribeChannelList(Long customerId);
}
