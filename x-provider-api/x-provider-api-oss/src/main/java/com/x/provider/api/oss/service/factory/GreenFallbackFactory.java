package com.x.provider.api.oss.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.oss.model.ao.AttributeGreenRpcAO;
import com.x.provider.api.oss.model.dto.AttributeGreenResultDTO;
import com.x.provider.api.oss.service.GreenRpcService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class GreenFallbackFactory implements FallbackFactory<GreenRpcService> {
    @Override
    public GreenRpcService create(Throwable throwable) {
        return new GreenRpcService() {
            @Override
            public R<Void> greenAttributeAsync(AttributeGreenRpcAO attribute) {
                return null;
            }

            @Override
            public R<AttributeGreenResultDTO> greenAttributeSync(AttributeGreenRpcAO attribute) {
                return null;
            }
        };
    }
}
