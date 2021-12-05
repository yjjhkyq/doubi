package com.x.provider.vod.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class VodUploadParamVO {
    @ApiModelProperty(value = "签名")
    private String signature;
    @ApiModelProperty(value = "视频路径")
    private String videoPath;
    @ApiModelProperty(value = "视频封面路径")
    private String coverPath;
    @ApiModelProperty(value = "文件名")
    private String fileName;
}
