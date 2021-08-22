package com.x.provider.api.vod.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.vod.model.ao.GetContentReviewResultAO;
import com.x.provider.api.vod.service.VodRpcService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class VodFallbackFactory implements FallbackFactory<VodRpcService> {

    @Override
    public VodRpcService create(Throwable throwable) {
        return new VodRpcService() {

            @Override
            public R<Void> contentReview(GetContentReviewResultAO getContentReviewResultAO) {
                return null;
            }
        };
    }
}
