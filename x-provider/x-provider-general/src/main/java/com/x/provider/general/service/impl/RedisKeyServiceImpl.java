package com.x.provider.general.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.provider.general.service.RedisKeyService;
import org.springframework.stereotype.Service;

@Service
public class RedisKeyServiceImpl implements RedisKeyService {
    private static final String KEY_PREFIX = "General";

    private static final String FULL_KEY = KEY_PREFIX + ":{}";

    private static final String SMS_BY_PHONE_NUMBER_TEMPLATE_ID = "Sms:Phone:{}:TemplateId:{}";

    @Override
    public String getSmsKey(String phoneNumber, String templateId) {
        return getFullKey(SMS_BY_PHONE_NUMBER_TEMPLATE_ID, phoneNumber, templateId);
    }

    private String getFullKey(CharSequence keyTemplate, Object... params){
        return StrUtil.format(FULL_KEY, StrUtil.format(keyTemplate, params));
    }


}
