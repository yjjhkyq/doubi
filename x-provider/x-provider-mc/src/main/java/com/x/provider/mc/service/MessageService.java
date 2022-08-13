package com.x.provider.mc.service;

import com.x.core.web.page.PageDomain;
import com.x.core.web.page.PageList;
import com.x.provider.api.mc.model.dto.SendMessageRequestDTO;
import com.x.provider.api.mc.model.event.MessageEvent;
import com.x.provider.mc.model.bo.GetConversationRequestBO;
import com.x.provider.mc.model.vo.MarkMessageAsReadRequestVO;
import com.x.provider.mc.model.domain.Conversation;
import com.x.provider.mc.model.domain.Group;
import com.x.provider.mc.model.domain.Message;
import com.x.provider.mc.model.bo.ConversationBO;
import com.x.provider.mc.model.bo.MessageBO;

import java.util.List;

public interface MessageService {
    PageList<Message> listMessage(String conversationId, Long sessionCustomerId, PageDomain pageDomain);
    MessageBO sendMessage(SendMessageRequestDTO sendMessageAO);
    PageList<Conversation> listConversation(Long ownerCustomerId, PageDomain pageDomain);
    void markMessageAsRead(MarkMessageAsReadRequestVO markMessageAsReadAO, Long ownerCustomerId);
    Conversation getConversation(GetConversationRequestBO getConversationRequestBO);
    Long getTotalUnreadMessageCount(Long ownerCustomerId);
    Group getGroup(Long id);
    List<Group> listGroup(List<Long> idList);
    void onSendMessage(MessageEvent sendMessageEvent);
    void initSystemConversation(Long ownerCustomerId);
    List<MessageBO> prepareMessage(List<Message> messageList);
    List<ConversationBO> prepare(List<Conversation> conversationList);
    ConversationBO prepare(Conversation conversation);
}
