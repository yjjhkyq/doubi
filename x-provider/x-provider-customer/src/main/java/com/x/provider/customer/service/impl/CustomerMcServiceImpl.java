package com.x.provider.customer.service.impl;

import com.x.provider.api.customer.model.event.CustomerInfoGreenEvent;
import com.x.provider.api.customer.model.event.FollowEvent;
import com.x.provider.api.mc.service.McHelper;
import com.x.provider.api.mc.service.MessageRpcService;
import com.x.provider.customer.enums.SystemCustomerAttributeName;
import com.x.provider.customer.service.CustomerMcService;
import com.x.provider.customer.service.CustomerService;
import org.springframework.stereotype.Service;

@Service
public class CustomerMcServiceImpl implements CustomerMcService {

    private final MessageRpcService messageRpcService;
    private final CustomerService customerService;

    public CustomerMcServiceImpl(MessageRpcService messageRpcService,
                                 CustomerService customerService){
        this.messageRpcService = messageRpcService;
        this.customerService = customerService;
    }

    @Override
    public void onCustomerInfoGreen(CustomerInfoGreenEvent event) {
        messageRpcService.sendMessage(McHelper.buildUserInfoGreenNotify(event.getCustomerId(), event.isPass()));
    }

    @Override
    public void onFollowEvent(FollowEvent followEvent) {
        if (FollowEvent.EventTypeEnum.FOLLOW.getValue().equals(followEvent.getEventType()) && !followEvent.isFirstFollow()){
            return;
        }
        String nickName = this.customerService.listCustomerAttribute(followEvent.getFromCustomerId()).get(SystemCustomerAttributeName.NICK_NAME.name());
        messageRpcService.sendMessage(McHelper.buildFansNotify(followEvent.getToCustomerId(), nickName));
    }
}
