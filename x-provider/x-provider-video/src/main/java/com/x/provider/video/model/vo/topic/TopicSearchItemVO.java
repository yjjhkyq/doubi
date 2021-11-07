package com.x.provider.video.model.vo.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class TopicSearchItemVO {
    @ApiModelProperty(value = "主题id")
    private long id;
    @ApiModelProperty(value = "主题标题")
    private String title;
}
