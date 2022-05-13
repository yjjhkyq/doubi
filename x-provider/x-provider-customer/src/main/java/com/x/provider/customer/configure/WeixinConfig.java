package com.x.provider.customer.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("weixin")
@Data
public class WeixinConfig {
    private String microAppId;

    private String microAppSecret;
}
