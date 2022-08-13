package com.x.provider.mc.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.x.provider.api.mc.model.protocol.CommonMessageBodyDTO;
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
    private Long fromCustomerId;
    @ApiModelProperty(value = "消息接收人用户id")
    private Long toCustomerId = 0L;
    @ApiModelProperty(value = "消息接收群组id")
    private Long toGroupId = 0L;
    @ApiModelProperty(value = "消息类型")
    private String messageType;
    @ApiModelProperty(value = "提示信息")
    private String alertMsg;
    @ApiModelProperty(value = "消息体")
    private String msgBody;
    @ApiModelProperty(value = "消息创建日期")
    private Date createdOnUtc;
    @ApiModelProperty(value = "消息创建日期, 毫秒")
    private Long createdTimestamp;
    @ApiModelProperty(value = "消息发送人头像")
    private String fromCustomerAvatarUrl;
    @ApiModelProperty(value = "消息发送人昵称")
    private String fromCustomerNickName;

    @ApiModelProperty(value = "文本消息内容")
    private CommonMessageBodyDTO textBody;
    @ApiModelProperty(value = "图片消息内容")
    private CommonMessageBodyDTO imageBody;
    @ApiModelProperty(value = "视频消息内容")
    private CommonMessageBodyDTO videoBody;
    @ApiModelProperty(value = "声音消息内容")
    private CommonMessageBodyDTO voiceBody;
}
