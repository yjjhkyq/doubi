package com.x.provider.mc.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class MarkMessageAsReadRequestVO {
    @ApiModelProperty(value = "会话id")
    private String conversationId;
}
