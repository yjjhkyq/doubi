package com.x.provider.video.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class StarVideoAO {
    @ApiModelProperty(value = "视频id")
    private long videoId;
    @ApiModelProperty(value = "true 点赞 反之取消点赞")
    private boolean star;
}
