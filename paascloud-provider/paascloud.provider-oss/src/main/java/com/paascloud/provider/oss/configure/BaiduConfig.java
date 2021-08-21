package com.paascloud.provider.oss.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Data
@Component
@ConfigurationProperties("baidu")
public class BaiduConfig {
    private String appId;
    private String apiKey;
    private String secretKey;
}
