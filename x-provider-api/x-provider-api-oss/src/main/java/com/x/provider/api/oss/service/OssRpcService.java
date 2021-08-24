package com.x.provider.api.oss.service;

import com.x.core.web.api.R;
import com.x.provider.api.oss.constants.ServiceNameConstants;
import com.x.provider.api.oss.service.factory.OssFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "ossService", value = ServiceNameConstants.OSS_SERVICE, fallbackFactory = OssFallbackFactory.class)
public interface OssRpcService {
    @GetMapping(ServiceNameConstants.OSS_RPC_URL_PREFIX + "/browser/url")
    R<String> getObjectBrowseUrl(@RequestParam("objectKey") String objectKey);

    @GetMapping(ServiceNameConstants.OSS_RPC_URL_PREFIX + "/browser/url/list")
    R<Map<String, String>> listObjectBrowseUrl(@RequestParam("objectKeys") List<String> objectKeys);
}
