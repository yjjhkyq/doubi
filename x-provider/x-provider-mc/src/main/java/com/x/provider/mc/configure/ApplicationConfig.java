package com.x.provider.mc.configure;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RefreshScope
public class ApplicationConfig {

    @Value("${centrifugo.web.socket.url:null}")
    private String centrifugoWebSocketUrl;

    @Value("${centrifugo.http.api.url:null}")
    private String centrifugoHttpApiUrl;

    @Value("${centrifugo.token.secret:null}")
    private String centrifugoTokenSecret;

    @Value("${centrifugo.api.key:null}")
    private String centrifugoApiKey;

}
