package com.x.provider.mc.service.impl;

import com.x.core.utils.JsonUtil;
import com.x.core.utils.SpringUtils;
import com.x.provider.api.mc.enums.ConversationType;
import com.x.provider.api.mc.model.ao.SendMessageRawAO;
import com.x.provider.mc.model.dto.ConnectInfoDTO;
import com.x.provider.mc.model.dto.ConversationDTO;
import com.x.provider.mc.model.dto.MessageDTO;
import com.x.provider.mc.service.MessageEngineService;
import com.x.provider.mc.service.WebSocketEngineService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageEngineServiceImpl implements MessageEngineService {

    private static final Integer TOKEN_LIVE_DAYS = 2;
    private static final String CHANNEL_FORMATTER = "{}_{}";

    private final Map<Integer, WebSocketEngineService> WEB_SOCKET_ENGINE_MAP = SpringUtils.getBanListOfType(WebSocketEngineService.class).stream()
            .collect(Collectors.toMap(item -> item.getWebSocketEngineType(), item -> item));

    @Override
    public void sendMessage(ConversationDTO conversation, MessageDTO message) {
        Map<String, Object> data = Map.of("conversation", conversation, "message", message);
        SendMessageRawAO sendMessageRawAO = SendMessageRawAO.builder()
                .jsonData(JsonUtil.toJSONString(data))
                .messageClass(message.getMessageClass())
                .messageType(message.getMessageType())
                .toCustomerId(message.getToCustomerId())
                .toGroupId(message.getToGroupId())
                .build();
        sendMessage(sendMessageRawAO);
    }

    @Override
    public void sendMessage(SendMessageRawAO sendMessageRawAO){
        Map<String, Object> msg = new HashMap<>();
        msg.put("messageClass", sendMessageRawAO.getMessageClass());
        msg.put("messageType", sendMessageRawAO.getMessageType());
        msg.put("jsonData", sendMessageRawAO.getJsonData());
        WEB_SOCKET_ENGINE_MAP.values().forEach(item -> {
            item.sendMessage(sendMessageRawAO.getToCustomerId(), sendMessageRawAO.getToGroupId(), JsonUtil.toJSONString(msg));
        });

    }

    @Override
    public List<ConnectInfoDTO> listConnectionInfo(Long customerId) {
        List<ConnectInfoDTO> result = new ArrayList<>();
        Long tokenExpireTime = System.currentTimeMillis() + Duration.ofDays(TOKEN_LIVE_DAYS).getSeconds() * 1000;
        WEB_SOCKET_ENGINE_MAP.values().forEach(item -> {
            ConnectInfoDTO connectInfoDTO = new ConnectInfoDTO();
            connectInfoDTO.setAuthenticationToken(item.authenticationToken(customerId.toString(), tokenExpireTime));
            connectInfoDTO.setWebSocketEngineType(item.getWebSocketEngineType());
            connectInfoDTO.setWebSocketUrl(item.getWebSocketUrl());
            connectInfoDTO.setSubscribeChannelList(List.of(item.getChannelName(customerId.toString(), ConversationType.C2C.getValue())));
            result.add(connectInfoDTO);
        });
        return result;
    }

}
