package com.x.provider.oss.model.vo.vod;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class VodUploadParamVO {
    @ApiModelProperty(value = "签名")
    private String signature;
    @ApiModelProperty(value = "文件名")
    private String fileName;
}
