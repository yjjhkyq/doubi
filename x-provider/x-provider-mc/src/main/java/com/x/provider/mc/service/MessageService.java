package com.x.provider.mc.service;

import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.api.mc.model.ao.SendMessageAO;
import com.x.provider.api.mc.model.event.MessageEvent;
import com.x.provider.mc.model.ao.MarkMessageAsReadAO;
import com.x.provider.mc.model.domain.Conversation;
import com.x.provider.mc.model.domain.Group;
import com.x.provider.mc.model.domain.Message;
import com.x.provider.mc.model.dto.ConversationDTO;
import com.x.provider.mc.model.dto.MessageDTO;
import com.x.provider.mc.model.vo.ConversationVO;

import java.util.List;
import java.util.Set;

public interface MessageService {
    PageList<Message> listMessage(String conversationId, Long sessionCustomerId, PageDomain pageDomain);
    Long sendMessage(SendMessageAO sendMessageAO);
    String getWebSocketUrl();
    String authenticationToken(Long customerId);
    Set<String> subscribeChannelList(Long customerId);
    PageList<Conversation> listConversation(Long ownerCustomerId, PageDomain pageDomain);
    void markMessageAsRead(MarkMessageAsReadAO markMessageAsReadAO, Long customerId);
    Conversation getConversation(String conversationId, Long customerId);
    Long getTotalUnreadMessageCount(Long ownerCustomerId);
    Group getGroup(Long id);
    List<Group> listGroup(List<Long> idList);
    void onSendMessage(MessageEvent sendMessageEvent);

    List<MessageDTO> prepareMessage(List<Message> messageList);
    List<ConversationDTO> prepare(List<Conversation> conversationList);
    ConversationDTO prepare(Conversation conversation);
}
