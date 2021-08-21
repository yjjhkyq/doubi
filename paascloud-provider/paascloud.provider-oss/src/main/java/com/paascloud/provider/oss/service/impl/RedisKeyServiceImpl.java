package com.paascloud.provider.oss.service.impl;

import cn.hutool.core.util.StrUtil;
import com.paascloud.provider.oss.service.RedisKeyService;
import org.springframework.stereotype.Service;

@Service
public class RedisKeyServiceImpl implements RedisKeyService {
    private static final String FULL_KEY = "Oss:{}";

    public static final String ATTRIBUTE_GREEN_RESULT = "AttributeGreenResult:ObjectKey:{}";
    public static final String ATTRIBUTE_GREEN_RPCAO = "AttributeGreenRpcAO:ObjectKey:";
    public static final String ATTRIBUTE_GREEN_LOCK_KEY = "AttributeGreen:{}";

    @Override
    public String getAttributeGreenResultKey(String objectKey) {
        return getFullKey(ATTRIBUTE_GREEN_RESULT, objectKey);
    }

    @Override
    public String getAttributeGreenRpcAOKey(String objectKey) {
        return getFullKey(ATTRIBUTE_GREEN_RPCAO, objectKey);
    }

    @Override
    public String getAttributeGreenLockKey(String objectKey) {
        return getFullKey(ATTRIBUTE_GREEN_LOCK_KEY, objectKey);
    }

    private String getFullKey(CharSequence keyTemplate, Object... params){
        return StrUtil.format(FULL_KEY, StrUtil.format(keyTemplate, params));
    }
}
