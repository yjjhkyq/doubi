package com.x.provider.mc.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SendImRequestAO {
    @ApiModelProperty(value = "提示消息")
    private String alertMsg;
    @ApiModelProperty(value = "消息体")
    private String msgBody;
    @ApiModelProperty(value = "消息类型 TEXT 文本消息 IMAGE 图片消息 VIDEO 视频信息 格式和腾讯云类似：https://cloud.tencent.com/document/product/269/2720")
    private String messageType;
    @ApiModelProperty(value = "接收人用户id")
    private Long toCustomerId = 0L;
    @ApiModelProperty(value = "接收群 组id")
    private Long toGroupId = 0L;
}
