package com.x.provider.mc.service;

import java.util.Map;

public interface ImEngineService {
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
     * @param channel 频道名称
     * @param data 要发送的数据
     */
    void publish(String channel, Map<String, Object> data);
}
