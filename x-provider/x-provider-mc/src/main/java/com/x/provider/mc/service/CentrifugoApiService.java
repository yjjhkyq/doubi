package com.x.provider.mc.service;


import java.util.List;
import java.util.Map;

public interface CentrifugoApiService {
    String authenticationToken(String tokenHmacSecretKey, long expireAt, String subject);
    void publish(String url, String apiKey, String channel, Map<String, Object> data);
    List<String> listChannels(String url, String apiKey);
}
