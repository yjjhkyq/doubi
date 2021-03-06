package com.x.provider.mc.service.impl;

import com.x.provider.mc.configure.ApplicationConfig;
import com.x.provider.mc.service.CentrifugoApiService;
import com.x.provider.mc.service.CentrifugoEngineService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CentrifugoEngineServiceImpl implements CentrifugoEngineService {

    private final ApplicationConfig applicationConfig;
    private final CentrifugoApiService centrifugoApiService;

    public CentrifugoEngineServiceImpl(ApplicationConfig applicationConfig,
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
