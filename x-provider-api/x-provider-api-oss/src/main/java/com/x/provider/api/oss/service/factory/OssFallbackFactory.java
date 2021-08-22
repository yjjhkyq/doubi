package com.x.provider.api.oss.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.oss.service.OssRpcService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OssFallbackFactory implements FallbackFactory<OssRpcService> {

    @Override
    public OssRpcService create(Throwable throwable) {
        return new OssRpcService() {
            @Override
            public R<String> getOjectBrowseUrl(String objectKey) {
                return null;
            }

            @Override
            public R<Map<String, String>> listOjectBrowseUrl(List<String> objectKeys) {
                return null;
            }
        };
    }
}
