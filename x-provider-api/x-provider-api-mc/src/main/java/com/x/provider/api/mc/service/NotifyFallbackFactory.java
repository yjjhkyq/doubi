package com.x.provider.api.mc.service;

import com.x.core.web.api.R;
import com.x.provider.api.mc.model.ao.SendMessageAO;
import com.x.provider.api.mc.model.ao.SendMessageRawAO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class NotifyFallbackFactory implements FallbackFactory<MessageRpcService> {

    @Override
    public MessageRpcService create(Throwable throwable) {
        return new MessageRpcService() {
            @Override
            public R<Long> sendMessage(SendMessageAO sendNotifyAO) {
                return R.ok(0L);
            }

            @Override
            public R<Long> sendMessageRaw(SendMessageRawAO sendMessageAO) {
                return R.ok(0L);
            }
        };
    }
}
