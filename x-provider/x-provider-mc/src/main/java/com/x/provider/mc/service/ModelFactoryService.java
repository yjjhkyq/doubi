package com.x.provider.mc.service;

import com.x.provider.mc.model.domain.Conversation;
import com.x.provider.mc.model.vo.ConversationVO;

import java.util.List;

public interface ModelFactoryService {
    ConversationVO prepare(Conversation conversation);
    List<ConversationVO> prepare(List<Conversation> conversationList);
}
