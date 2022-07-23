package com.x.provider.oss.configure;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
public class TencentOssConfig {

    @Value("${tencent.app.secret-id}")
    private String appSecretId;

    @Value("${tencent.app.secret-key}")
    private String appSecretKey;

    @Value("${tencent.oss.bucket-customer}")
    private String ossBucketCustomer;

    @Value("${tencent.vod.task-stream-name}")
    private String vodTaskStreamName;

    public static final String AP_CHENGDU = "ap-chengdu";
    public static final String DOMAIN_QCLOUD = "myqcloud.com";
}
