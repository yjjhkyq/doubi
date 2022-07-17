package com.x.provider.mc.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.provider.mc.service.RedisKeyService;
import org.springframework.stereotype.Service;

@Service
public class RedisKeyServiceImpl implements RedisKeyService {
    private static final String FULL_KEY = "Mc:{}";

    public static final String MESSAGE_SENDER_SYSTEM_VO = "VO:Message:Sender:System";

    private static final String SMS_BY_PHONE_NUMBER_TEMPLATE_ID = "Phone:{}:TemplateId:{}";

    @Override
    public String getMessageSenderSystemVOKey() {
        return getFullKey(MESSAGE_SENDER_SYSTEM_VO);
    }

    @Override
    public String getSmsKey(String phoneNumber, String templateId) {
        return getFullKey(SMS_BY_PHONE_NUMBER_TEMPLATE_ID, phoneNumber, templateId);
    }

    private String getFullKey(CharSequence keyTemplate, Object... params){
        return StrUtil.format(FULL_KEY, StrUtil.format(keyTemplate, params));
    }

}
