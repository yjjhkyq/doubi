package com.x.provider.video.model.ao.homepage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ApiModel
@Data
public class CreateVideoAO {
    @ApiModelProperty(value = "视频标题")
    @NotBlank
    private String title;
    @ApiModelProperty(value = "视频标文件 id")
    @NotBlank
    private String fileId;
    @ApiModelProperty(value = "@用户id，此工作暂时先不做")
    private List<Long> atUsers;
}
