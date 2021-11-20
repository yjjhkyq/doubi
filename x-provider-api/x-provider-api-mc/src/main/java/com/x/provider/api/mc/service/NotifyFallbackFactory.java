package com.x.provider.api.mc.service;

import com.x.core.web.api.R;
import com.x.provider.api.mc.model.ao.SendNotifyAO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class NotifyFallbackFactory implements FallbackFactory<NotifyRpcService> {

    @Override
    public NotifyRpcService create(Throwable throwable) {
        return new NotifyRpcService() {
            @Override
            public R<Void> sendNotify(SendNotifyAO sendNotifyAO) {
                return R.ok();
            }
        };
    }
}
