package com.x.provider.general.service;

public interface RedisKeyService {
    //Sms
    String getSmsKey(String phoneNumber, String templateId);

}
