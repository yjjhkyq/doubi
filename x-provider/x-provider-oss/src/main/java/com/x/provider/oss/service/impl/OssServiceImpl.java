package com.x.provider.oss.service.impl;

import com.x.provider.oss.service.OssService;
import com.x.provider.oss.service.TencentOssService;
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
    public String getObjectBrowseUrl(String objectKey) {
        return tencentOssService.getObjectBrowseUrl(objectKey);
    }

    @Override
    public Map<String, String> listObjectBrowseUrl(List<String> objectKeys) {
        return tencentOssService.listObjectBrowseUrl(objectKeys);
    }
}
