package com.x.provider.mc.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class MarkMessageAsReadAO {
    @ApiModelProperty(value = "会话id")
    private String conversationId;
}
