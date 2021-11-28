package com.x.provider.mc.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SendImAO {
    @ApiModelProperty(value = "提示消息")
    private String alertMsg;
    @ApiModelProperty(value = "消息体")
    private String msgBody;
    @ApiModelProperty(value = "消息类型")
    private String messageType;
    @ApiModelProperty(value = "接收人用户id")
    private Long targetCustomerId;
}
