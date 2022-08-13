package com.x.provider.oss.service;

import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.Map;

public interface OssService {
    String getObjectBrowseUrl(String objectKey);
    Map<String, String> listObjectBrowseUrl(List<String> objectKeys);
    static String getObjectKey(long customerId, String fileName){
        return StrUtil.format("/{}/{}", customerId, fileName);
    }
}
