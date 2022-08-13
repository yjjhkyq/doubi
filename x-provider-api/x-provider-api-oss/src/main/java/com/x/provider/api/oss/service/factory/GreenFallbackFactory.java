package com.x.provider.api.oss.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.oss.model.dto.oss.AttributeGreenRequestDTO;
import com.x.provider.api.oss.model.dto.oss.GreenRequestDTO;
import com.x.provider.api.oss.model.dto.oss.AttributeGreenResultDTO;
import com.x.provider.api.oss.service.GreenRpcService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class GreenFallbackFactory implements FallbackFactory<GreenRpcService> {
    @Override
    public GreenRpcService create(Throwable throwable) {
        return new GreenRpcService() {
            @Override
            public R<Void> greenAttributeAsync(AttributeGreenRequestDTO attribute) {
                return null;
            }

            @Override
            public R<AttributeGreenResultDTO> greenAttributeSync(AttributeGreenRequestDTO attribute) {
                return null;
            }

            @Override
            public R<String> greenSync(GreenRequestDTO greenAO) {
                return null;
            }
        };
    }
}
