package com.paascloud.provider.oss.service.impl;

import com.paascloud.provider.oss.service.OssService;
import com.paascloud.provider.oss.service.TencentOssService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OssServiceImpl implements OssService {

    private final TencentOssService tencentOssService;

    public OssServiceImpl(TencentOssService tencentOssService){
        this.tencentOssService = tencentOssService;
    }

    @Override
    public String getOjectBrowseUrl(String objectKey) {
        return tencentOssService.getOjectBrowseUrl(objectKey);
    }

    @Override
    public Map<String, String> listOjectBrowseUrl(List<String> objectKeys) {
        return tencentOssService.listOjectBrowseUrl(objectKeys);
    }
}
