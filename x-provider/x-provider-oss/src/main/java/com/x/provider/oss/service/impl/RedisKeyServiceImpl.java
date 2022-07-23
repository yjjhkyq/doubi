package com.x.provider.oss.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.provider.oss.service.RedisKeyService;
import org.springframework.stereotype.Service;

@Service
public class RedisKeyServiceImpl implements RedisKeyService {
    private static final String FULL_KEY = "Oss:{}";

    public static final String ATTRIBUTE_GREEN_RESULT = "AttributeGreenResult:ObjectKey:{}";
    public static final String ATTRIBUTE_GREEN_RPCAO = "AttributeGreenRpcAO:ObjectKey:";
    public static final String ATTRIBUTE_GREEN_LOCK_KEY = "AttributeGreen:{}";

    public static final String CONTENT_REVIEW_NOTIFY_LOCK_KEY = "ContentReviewNotify:Lock:{}";
    public static final String CONTENT_REVIEW_NOTIFY_URL = "ContentReviewNotify:Url:{}";

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

    @Override
    public String getContentReviewNotifyLockKey(String fileId) {
        return getFullKey(CONTENT_REVIEW_NOTIFY_LOCK_KEY, fileId);
    }

    @Override
    public String getContentReviewNotifyUrl(String fileId) {
        return getFullKey(fileId);
    }

}
