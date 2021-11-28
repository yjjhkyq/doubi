package com.x.provider.mc.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO {
    @ApiModelProperty(value = "消息id")
    private Long id;
    @ApiModelProperty(value = "消息发送人用户id")
    private Long senderUid;
    @ApiModelProperty(value = "消息接收目标id")
    private Long targetId;
    @ApiModelProperty(value = "消息类型")
    private String messageType;
    @ApiModelProperty(value = "提示信息")
    private String alertMsg;
    @ApiModelProperty(value = "消息体")
    private String msgBody;
    @ApiModelProperty(value = "消息创建日期")
    private Date createdOnUtc;
}
