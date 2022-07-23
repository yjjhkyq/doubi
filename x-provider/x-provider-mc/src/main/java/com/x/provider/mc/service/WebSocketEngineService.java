package com.x.provider.mc.service;

public interface WebSocketEngineService {
    /**
     * 获取web socket 地址
     * @return web socket 地址
     */
    String getWebSocketUrl();

    /**
     * 获取授权token
     * @param subject 用户id
     * @param expTime 过期时间，0表示用系统默认过期时间
     * @return token
     */
    String authenticationToken(String subject, long expTime);

    /**
     * 发送消息
     */
    void sendMessage(Long toCustomerId, Long toGroupId, String message);

    String getChannelName(String targetId, Integer conversationType);

    Integer getWebSocketEngineType();
}
