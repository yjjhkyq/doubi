package com.x.provider.mc.service.impl;

import com.x.provider.mc.configure.ApplicationConfig;
import com.x.provider.mc.service.CentrifugoApiService;
import com.x.provider.mc.service.ImEngineService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ImEngineServiceImpl implements ImEngineService {

    private final ApplicationConfig applicationConfig;
    private final CentrifugoApiService centrifugoApiService;

    public ImEngineServiceImpl(ApplicationConfig applicationConfig,
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

    @Override
    public void publish(String channel, Map<String, Object> data) {
        centrifugoApiService.publish(applicationConfig.getCentrifugoHttpApiUrl(), applicationConfig.getCentrifugoApiKey(), channel, data);
    }
}
