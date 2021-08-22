package com.x.provider.oss.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.Map;

public interface OssService {
    String getOjectBrowseUrl(String objectKey);
    Map<String, String> listOjectBrowseUrl(List<String> objectKeys);
    static String getObjectKey(long customerId, String extName){
        return StrUtil.format("/{}/{}.{}", customerId, IdUtil.simpleUUID(), extName);
    }
}
