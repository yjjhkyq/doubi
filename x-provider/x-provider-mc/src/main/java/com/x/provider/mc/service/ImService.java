package com.x.provider.mc.service;

import com.x.provider.mc.enums.ChannelTypeEnum;

import java.util.Map;

public interface ImService {
    void sendMessage(ChannelTypeEnum channelTypeEnum, Long senderUserId, Long receiverUserId, Map<String, Object> data);
    String authenticationToken(Long userId);
    String getWebSocketUrl();
    String getChannelName(Long receiverUserId, ChannelTypeEnum channelTypeEnum);
}
