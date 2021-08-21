package com.paascloud.provider.vod.service;

public interface RedisKeyService {
    String getContentReviewNotifyLockKey(String fileId);
    String getContentReviewNotifyUrl(String fileId);
}
