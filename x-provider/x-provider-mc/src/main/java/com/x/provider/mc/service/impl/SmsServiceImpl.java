package com.x.provider.mc.service.impl;

import com.x.core.utils.ApiAssetUtil;
import com.x.provider.api.mc.enums.McErrorEnum;
import com.x.provider.api.mc.enums.SmsTemplateEnum;
import com.x.provider.mc.configure.ApplicationConfig;
import com.x.provider.mc.mapper.SmsMapper;
import com.x.provider.mc.model.domain.Sms;
import com.x.provider.mc.service.RedisKeyService;
import com.x.provider.mc.service.SmsEngineService;
import com.x.provider.mc.service.SmsService;
import com.x.redis.service.RedisService;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SmsServiceImpl implements SmsService {

    private final SmsMapper smsMapper;
    private final RedisKeyService redisKeyService;
    private final RedisService redisService;
    private final ApplicationConfig applicationConfig;
    private final SmsEngineService smsEngineService;

    public SmsServiceImpl(SmsMapper smsMapper,
                          RedisKeyService redisKeyService,
                          RedisService redisService,
                          ApplicationConfig applicationConfig,
                          SmsEngineService smsEngineService){
        this.smsMapper = smsMapper;
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
        this.applicationConfig = applicationConfig;
        this.smsEngineService = smsEngineService;
    }

    @Override
    public void sendVerificationCode(String phoneNumber) {
        int code = RandomUtils.nextInt(1000, 9999);
        //发送短信
        SmsTemplateEnum templateId = SmsTemplateEnum.VERIFICATION_CODE;
        smsEngineService.sendSms(templateId, phoneNumber, String.valueOf(code));
        redisService.setCacheObject(redisKeyService.getSmsKey(phoneNumber, templateId.name()), String.valueOf(code), Duration.ofMinutes(applicationConfig.getVerificationCodeExpireMinute()));
        smsMapper.insert(Sms.builder().phoneNumberSet(phoneNumber).templateId(templateId.name()).templateParamSet(String.valueOf(code)).build());
    }

    @Override
    public void validateVerificationCode(String phoneNumber, String sms) {
        SmsTemplateEnum templateId = SmsTemplateEnum.VERIFICATION_CODE;
        String actualSms = redisService.getCacheObject(redisKeyService.getSmsKey(phoneNumber, templateId.name()), String.class);
        ApiAssetUtil.isTrue(sms.equals(actualSms),  McErrorEnum.VERIFICATION_CODE_ERROR);
        redisService.deleteObject(redisKeyService.getSmsKey(phoneNumber, templateId.name()));
    }
}
