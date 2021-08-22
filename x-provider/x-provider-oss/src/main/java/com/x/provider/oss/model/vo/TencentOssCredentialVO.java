package com.x.provider.oss.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "腾讯云对象存储上传参数")
public class TencentOssCredentialVO {
    private String tmpSecretId;
    private String tmpSecretKey;
    private String sessionToken;
    private String bucketName;
    private long expiredTime;
    private String allowPrefix;
    private String regionName;
}
