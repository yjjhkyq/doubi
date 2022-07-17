package com.x.provider.mc.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationVO {
    @ApiModelProperty(value = "用户id")
    private Long customerId;
    @ApiModelProperty(value = "群组id")
    private Long groupId;
    @ApiModelProperty(value = "会话拥有者用户id")
    private Long ownerCustomerId;
    @ApiModelProperty(value = "最新消息提示")
    private String alertMsg;
    @ApiModelProperty(value = "会话类型 1 1对于1消息 2 群组消息")
    private Integer conversationType;
    @ApiModelProperty(value = "消息未读数")
    private Long unreadCount;
    @ApiModelProperty(value = "显示顺序，值越大越靠前")
    private Long displayOrder;
    @ApiModelProperty(value = "会话id")
    private String conversationId;
    @ApiModelProperty(value = "会话展示名称")
    private String showName;
    @ApiModelProperty(value = "会话展示头像")
    private String faceUrl;
}
