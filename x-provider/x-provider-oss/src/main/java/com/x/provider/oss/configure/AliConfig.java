package com.x.provider.oss.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: liushenyi
 * @date: 2021/11/11/15:15
 */
@Data
@Component
@ConfigurationProperties("ali")
public class AliConfig {
    private String greenRegion;
    private String accessKeyId;
    private String accessKeySecret;
}
