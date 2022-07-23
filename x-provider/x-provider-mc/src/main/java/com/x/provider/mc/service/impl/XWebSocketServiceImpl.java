package com.x.provider.mc.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.x.provider.mc.configure.ApplicationConfig;
import com.x.provider.mc.enums.WebSocketEngineTypeEnum;
import com.x.provider.mc.model.dto.ConnectInfoDTO;
import com.x.provider.mc.model.event.SendMessageEvent;
import com.x.provider.mc.service.WebSocketEngineService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author: liushenyi
 * @date: 2022/07/22/17:17
 */
@Service("xWebSocketServiceImpl")
public class XWebSocketServiceImpl implements WebSocketEngineService {

    private final XWebSocketEndPoint xWebSocketEndPoint;
    private final ApplicationConfig applicationConfig;

    public XWebSocketServiceImpl(XWebSocketEndPoint xWebSocketEndPoint,
                                 ApplicationConfig applicationConfig){
        this.xWebSocketEndPoint = xWebSocketEndPoint;
        this.applicationConfig = applicationConfig;
    }

    @Override
    public String getWebSocketUrl() {
        return applicationConfig.getXWebSocketUrl();
    }

    @Override
    public String authenticationToken(String subject, long expTime) {
        return xWebSocketEndPoint.getXWebSocketAuthService().authenticationToken(expTime, subject);
    }

    @Override
    public void sendMessage(Long toCustomerId, Long toGroupId, String message) {
        xWebSocketEndPoint.sendMessage(new SendMessageEvent(toCustomerId, toGroupId, message));
    }

    @Override
    public String getChannelName(String targetId, Integer conversationType) {
        return "";
    }

    @Override
    public Integer getWebSocketEngineType() {
        return WebSocketEngineTypeEnum.X.getValue();
    }
}
