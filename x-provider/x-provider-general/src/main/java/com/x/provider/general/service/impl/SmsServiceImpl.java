package com.x.provider.general.service.impl;

import com.x.core.utils.ApiAssetUtil;
import com.x.provider.api.general.enums.SmsTemplateEnum;
import com.x.provider.general.configure.GeneralConfig;
import com.x.provider.general.enums.GeneralErrorEnum;
import com.x.provider.general.mapper.SmsMapper;
import com.x.provider.general.model.domain.Sms;
import com.x.provider.general.service.RedisKeyService;
import com.x.provider.general.service.SmsService;
import com.x.redis.service.RedisService;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SmsServiceImpl implements SmsService {

    private final SmsMapper smsMapper;
    private final RedisKeyService redisKeyService;
    private final RedisService redisService;
    private final GeneralConfig generalConfig;

    public SmsServiceImpl(SmsMapper smsMapper,
                          RedisKeyService redisKeyService,
                          RedisService redisService,
                          GeneralConfig generalConfig){
        this.smsMapper = smsMapper;
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
        this.generalConfig = generalConfig;
    }

    @Override
    public void sendVerificationCode(String phoneNumber) {
        int code = RandomUtils.nextInt(1000, 9999);
        //发送短信
        SmsTemplateEnum templateId = SmsTemplateEnum.VERIFICATION_CODE;
        redisService.setCacheObject(redisKeyService.getSmsKey(phoneNumber, templateId.name()), String.valueOf(code), Duration.ofMinutes(generalConfig.getVerificationCodeExpireMinute()));
        smsMapper.insert(Sms.builder().phoneNumberSet(phoneNumber).templateId(templateId.name()).templateParamSet(String.valueOf(code)).build());
    }

    @Override
    public void validateVerificationCode(String phoneNumber, String sms) {
        SmsTemplateEnum templateId = SmsTemplateEnum.VERIFICATION_CODE;
        String actualSms = redisService.getCacheObject(redisKeyService.getSmsKey(phoneNumber, templateId.name()));
        ApiAssetUtil.isTrue(sms.equals(actualSms),  GeneralErrorEnum.VERIFICATION_CODE_ERROR);
        redisService.deleteObject(redisKeyService.getSmsKey(phoneNumber, templateId.name()));
    }
}
