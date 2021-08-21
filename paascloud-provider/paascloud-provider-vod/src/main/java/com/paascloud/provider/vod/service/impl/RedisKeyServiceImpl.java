package com.paascloud.provider.vod.service.impl;

import cn.hutool.core.util.StrUtil;
import com.paascloud.provider.vod.service.RedisKeyService;
import org.springframework.stereotype.Service;

@Service
public class RedisKeyServiceImpl implements RedisKeyService {
    private static final String FULL_KEY = "Vod:{}";
    public static final String CONTENT_REVIEW_NOTIFY_LOCK_KEY = "ContentReviewNotify:Lock:{}";
    public static final String CONTENT_REVIEW_NOTIFY_URL = "ContentReviewNotify:Url:{}";

    @Override
    public String getContentReviewNotifyLockKey(String fileId) {
        return getFullKey(CONTENT_REVIEW_NOTIFY_LOCK_KEY, fileId);
    }

    @Override
    public String getContentReviewNotifyUrl(String fileId) {
        return getFullKey(fileId);
    }

    private String getFullKey(CharSequence keyTemplate, Object... params){
        return StrUtil.format(FULL_KEY, StrUtil.format(keyTemplate, params));
    }
}
