package com.x.provider.mc.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.core.utils.CompareUtils;
import com.x.provider.api.mc.enums.ConversationType;
import com.x.provider.mc.configure.ApplicationConfig;
import com.x.provider.mc.enums.WebSocketEngineTypeEnum;
import com.x.provider.mc.service.CentrifugoApiService;
import com.x.provider.mc.service.WebSocketEngineService;
import org.springframework.stereotype.Service;

@Service("centrifugoWebsocketServiceImpl")
public class CentrifugoWebsocketServiceImpl implements WebSocketEngineService {

    private final ApplicationConfig applicationConfig;
    private final CentrifugoApiService centrifugoApiService;

    public CentrifugoWebsocketServiceImpl(ApplicationConfig applicationConfig,
                                          CentrifugoApiService centrifugoApiService){
        this.applicationConfig = applicationConfig;
        this.centrifugoApiService = centrifugoApiService;
    }

    @Override
    public String getWebSocketUrl() {
        return applicationConfig.getCentrifugoWebSocketUrl();
    }

    @Override
    public String authenticationToken(String subject, long expTime) {
        return centrifugoApiService.authenticationToken(applicationConfig.getCentrifugoTokenSecret(), expTime, subject);
    }

    public void publish(String channel, String data) {
        centrifugoApiService.publish(applicationConfig.getCentrifugoHttpApiUrl(), applicationConfig.getCentrifugoApiKey(), channel, data);
    }

    @Override
    public void sendMessage(Long toCustomerId, Long toGroupId, String message) {
        publish(getChannelName(toCustomerId, toGroupId), message);
    }

    @Override
    public String getChannelName(String targetId, Integer conversationType){
        return StrUtil.format("{}_{}", ConversationType.valueOf(conversationType).name(), targetId);
    }

    @Override
    public Integer getWebSocketEngineType() {
        return WebSocketEngineTypeEnum.CENTRIFUGO.getValue();
    }

    public String getChannelName(Long customerId, Long groupId) {
        if (CompareUtils.gtZero(customerId)){
            return getChannelName(customerId.toString(), ConversationType.C2C.getValue());
        }
        return getChannelName(groupId.toString(), ConversationType.GROUP.getValue());
    }
}
