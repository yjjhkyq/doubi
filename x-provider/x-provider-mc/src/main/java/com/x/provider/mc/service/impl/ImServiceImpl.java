package com.x.provider.mc.service.impl;

import com.x.provider.mc.enums.ChannelTypeEnum;
import com.x.provider.mc.service.ImEngineService;
import com.x.provider.mc.service.ImService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImServiceImpl implements ImService {

    private final ImEngineService imEngineService;

    public ImServiceImpl(ImEngineService imEngineService){
        this.imEngineService = imEngineService;
    }

    @Override
    public void sendMessage(ChannelTypeEnum channelTypeEnum, Long senderUserId, Long receiverUserId, Map<String, Object> data) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("senderUserId", senderUserId);
        msg.put("receiverUserId", receiverUserId);
        msg.put("body", data);
        this.imEngineService.publish(getChannelName(receiverUserId, channelTypeEnum), msg);
    }

    @Override
    public String authenticationToken(Long userId) {
        return imEngineService.authenticationToken(userId.toString(), 0);
    }

    @Override
    public String getWebSocketUrl() {
        return imEngineService.getWebSocketUrl();
    }

    public String getChannelName(Long receiverUserId, ChannelTypeEnum channelTypeEnum){
        return channelTypeEnum.name() + receiverUserId;
    }
}
