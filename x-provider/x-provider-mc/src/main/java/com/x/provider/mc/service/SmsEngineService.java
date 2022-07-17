package com.x.provider.mc.service;

import com.x.provider.api.mc.enums.SmsTemplateEnum;

public interface SmsEngineService {
    void sendSms(SmsTemplateEnum smsTemplate, String phone, String templateParamSet);
}
