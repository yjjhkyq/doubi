package com.x.provider.mc.service.impl;

import com.x.provider.api.customer.service.CustomerRpcService;
import com.x.provider.mc.model.domain.Conversation;
import com.x.provider.mc.model.vo.ConversationVO;
import com.x.provider.mc.service.ModelFactoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelFactoryServiceImpl implements ModelFactoryService {

    private final CustomerRpcService customerRpcService;

    public ModelFactoryServiceImpl(CustomerRpcService customerRpcService){
        this.customerRpcService = customerRpcService;
    }

    @Override
    public ConversationVO prepare(Conversation conversation) {

        return null;
    }

    @Override
    public List<ConversationVO> prepare(List<Conversation> conversationList) {
        return null;
    }
}
