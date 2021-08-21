package com.paascloud.provider.oss.configure;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("tencent.oss")
@Data
public class TencentOssConfig {
    private String secretId;

    private String secretKey;

    private String bucketCustomer;

    public static final String AP_CHENGDU = "ap-chengdu";
    public static final String DOMAIN_QCLOUD = "myqcloud.com";
}
