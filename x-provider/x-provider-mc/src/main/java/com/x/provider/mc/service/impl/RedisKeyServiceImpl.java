package com.x.provider.mc.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.provider.mc.service.RedisKeyService;
import org.springframework.stereotype.Service;

@Service
public class RedisKeyServiceImpl implements RedisKeyService {
    private static final String FULL_KEY = "Mc:{}";

    public static final String MESSAGE_SENDER_SYSTEM_VO = "VO:Message:Sender:System";

    @Override
    public String getMessageSenderSystemVOKey() {
        return getFullKey(MESSAGE_SENDER_SYSTEM_VO);
    }

    private String getFullKey(CharSequence keyTemplate, Object... params){
        return StrUtil.format(FULL_KEY, StrUtil.format(keyTemplate, params));
    }

}
