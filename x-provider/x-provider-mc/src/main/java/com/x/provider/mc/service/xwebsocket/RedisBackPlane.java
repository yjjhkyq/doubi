package com.x.provider.mc.service.xwebsocket;

import cn.hutool.json.JSONUtil;
import com.x.core.utils.JsonUtil;
import com.x.provider.mc.model.event.SendMessageEvent;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * @author: liushenyi
 * @date: 2022/07/22/18:47
 */
@Service
public class RedisBackPlane {

    public void onSendMessage(String sendMessageEvent){
        String jsonStr = sendMessageEvent;
        XWebSocketEndPointCore.onSendMessageEvent(JSONUtil.toBean(jsonStr, SendMessageEvent.class));
    }

}
