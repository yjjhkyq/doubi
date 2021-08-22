package com.paascloud.provider.api.oss.service;

import com.paascloud.core.web.api.R;
import com.paascloud.provider.api.oss.constants.ServiceNameConstants;
import com.paascloud.provider.api.oss.service.factory.OssFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "ossService", value = ServiceNameConstants.OSS_SERVICE, fallbackFactory = OssFallbackFactory.class)
public interface OssRpcService {
    @GetMapping(ServiceNameConstants.OSS_RPC_URL_PREFIX + "/getOjectBrowseUrl")
    R<String> getOjectBrowseUrl(@RequestParam("objectKey") String objectKey);

    @GetMapping(ServiceNameConstants.OSS_RPC_URL_PREFIX + "/listOjectBrowseUrl")
    R<Map<String, String>> listOjectBrowseUrl(@RequestParam("objectKeys") List<String> objectKeys);
}