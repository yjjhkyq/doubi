package com.x.provider.api.mc.service;

import com.x.core.web.api.R;
import com.x.provider.api.mc.model.ao.SendMessageAO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class NotifyFallbackFactory implements FallbackFactory<MessageRpcService> {

    @Override
    public MessageRpcService create(Throwable throwable) {
        return new MessageRpcService() {
            @Override
            public R<Void> sendMessage(SendMessageAO sendNotifyAO) {
                return R.ok();
            }
        };
    }
}
