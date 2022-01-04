package com.x.provider.mc.service;

import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.api.mc.model.ao.SendMessageAO;
import com.x.provider.mc.model.domain.Message;
import com.x.provider.mc.model.domain.MessageReadBadge;
import com.x.provider.mc.model.domain.MessageSenderSystem;

import java.util.List;
import java.util.Set;

public interface MessageService {
    List<MessageSenderSystem> listMessageSenderSystem();
    PageList<Message> readMessage(Long customerId, Long senderUid, PageDomain pageDomain);
    void sendMessage(SendMessageAO notify);
    List<MessageReadBadge> listMessageReadBadge(Long customerId);

    String getWebSocketUrl();
    String authenticationToken(Long customerId);
    Set<String> subscribeChannelList(Long customerId);
}
