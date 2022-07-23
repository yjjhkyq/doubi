package com.x.provider.mc.configure;

import com.x.core.utils.SpringUtils;
import com.x.provider.mc.component.McConstant;
import com.x.provider.mc.service.xwebsocket.RedisBackPlane;
import com.x.provider.mc.service.xwebsocket.XWebSocketEndPointCore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class McRedisConfig {

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(XWebSocketEndPointCore.PATTERN_SEND_MESSAGE));
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(SpringUtils.getBean(RedisBackPlane.class), "onSendMessage");
    }

}
