package com.x.provider.oss.service;

public interface RedisKeyService {
    String getAttributeGreenResultKey(String objectKey);
    String getAttributeGreenRpcAOKey(String objectKey);
    String getAttributeGreenLockKey(String objectKey);

    String getContentReviewNotifyLockKey(String fileId);
    String getContentReviewNotifyUrl(String fileId);
}
