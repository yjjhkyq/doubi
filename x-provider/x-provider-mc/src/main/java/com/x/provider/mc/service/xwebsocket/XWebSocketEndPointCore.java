package com.x.provider.mc.service.xwebsocket;

import com.x.core.utils.CompareUtils;
import com.x.provider.mc.model.event.SendMessageEvent;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: liushenyi
 * @date: 2022/07/22/15:03
 */
@Slf4j
public class XWebSocketEndPointCore {

    public static final String PATTERN_SEND_MESSAGE = "send-message";
    public static final String PING = "ping";
    public static final String PONG = "pong";

    private static final ConcurrentHashMap<String, XWebSocketEndPointCore> CLIENT_MAP = new ConcurrentHashMap<>();

    private Session session;

    private String subject;

    private XWebSocketAuthService xWebSocketAuthService;

    public XWebSocketEndPointCore(XWebSocketAuthService xWebSocketAuthService){
        this.xWebSocketAuthService = xWebSocketAuthService;
    }

    /**
     * 连接建立成功调用的方法
     */
    public void onOpen(Session session, String token) {
        final String subject = xWebSocketAuthService.getSubject(token);
        this.session = session;
        this.subject = subject;
        CLIENT_MAP.put(subject, this);
    }

    /**
     * 连接关闭调用的方法
     */
    public void onClose() {
        if (subject == null){
            return;
        }
        CLIENT_MAP.remove(subject);
    }

    /**
     * 用户异常调用
     *
     * @param session
     * @param error
     */
    public void onError(Session session, Throwable error) {
    }

    public void onMessage(String message, Session session) throws IOException {
        if (PING.equals(message)){
            sendMessage(PONG);
        }
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static void sendMessage(String customerId, String message){
        final XWebSocketEndPointCore webSocketServer = CLIENT_MAP.get(customerId);
        if (webSocketServer != null){
            try {
                webSocketServer.sendMessage(message);
            }
            catch (IOException e){
            }
        }
    }

    public static void sendMessage(SendMessageEvent sendMessageEvent) {
        if (CompareUtils.gtZero(sendMessageEvent.getToCustomerId())){
            sendMessage(sendMessageEvent.getToCustomerId().toString(), sendMessageEvent.getMessage());
        }
    }

    public static void onSendMessageEvent(SendMessageEvent sendMessageEvent){
        sendMessage(sendMessageEvent);
    }
}
