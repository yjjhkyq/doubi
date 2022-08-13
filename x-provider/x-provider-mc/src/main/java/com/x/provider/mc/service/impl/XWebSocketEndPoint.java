package com.x.provider.mc.service.impl;

import cn.hutool.json.JSONUtil;
import com.x.core.utils.SpringUtils;
import com.x.provider.mc.configure.ApplicationConfig;
import com.x.provider.mc.model.event.SendMessageEvent;
import com.x.provider.mc.service.xwebsocket.XWebSocketAuthService;
import com.x.provider.mc.service.xwebsocket.XWebSocketEndPointCore;
import com.x.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * @author: liushenyi
 * @date: 2022/07/22/15:03
 */
@Slf4j
@ServerEndpoint("/ws/{token}")
@Component
public class XWebSocketEndPoint {

    private ApplicationConfig applicationConfig;
    private XWebSocketEndPointCore xWebSocketEndPointCore;
    private RedisService redisService;
    private XWebSocketAuthService webSocketAuthService;

    public XWebSocketEndPoint(){
        this.applicationConfig = SpringUtils.getBean(ApplicationConfig.class);
        this.redisService = SpringUtils.getBean(RedisService.class);
        this.webSocketAuthService = new XWebSocketAuthService(applicationConfig.getXWebSocketTokenSecret());

    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        log.debug("on open");
        this.xWebSocketEndPointCore = new XWebSocketEndPointCore(webSocketAuthService);
        xWebSocketEndPointCore.onOpen(session, token);
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        xWebSocketEndPointCore.onClose();
    }

    /**
     * 用户异常调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        xWebSocketEndPointCore.onError(session, error);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.debug("on message");
        xWebSocketEndPointCore.onMessage(message, session);
    }


    public void sendMessage(SendMessageEvent sendMessageEvent){
        redisService.convertAndSend(XWebSocketEndPointCore.PATTERN_SEND_MESSAGE, JSONUtil.toJsonStr(sendMessageEvent));
    }

    public XWebSocketEndPointCore getXWebSocketEndPointCore(){
        return xWebSocketEndPointCore;
    }

    public XWebSocketAuthService getXWebSocketAuthService(){
        return webSocketAuthService;
    }
}
