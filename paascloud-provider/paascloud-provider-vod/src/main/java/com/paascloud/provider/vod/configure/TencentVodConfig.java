package com.paascloud.provider.vod.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("tencent.vod")
@Data
public class TencentVodConfig {
    private String secretId;

    private String secretKey;

    private String taskStreamName;

    public static final String AP_CHENGDU = "ap-chengdu";
    public static final String DOMAIN_QCLOUD = "myqcloud.com";
}
