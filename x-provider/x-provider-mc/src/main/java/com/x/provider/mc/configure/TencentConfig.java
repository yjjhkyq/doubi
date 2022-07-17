package com.x.provider.mc.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("tencent")
@Data
public class TencentConfig {

    private String secretId;

    private String secretKey;

    private String smsSdkAppId;
}
