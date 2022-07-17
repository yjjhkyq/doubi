package com.x.provider.mc.service;

public interface RedisKeyService {
    String getMessageSenderSystemVOKey();

    //Sms
    String getSmsKey(String phoneNumber, String templateId);
}
