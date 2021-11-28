package com.x.provider.mc.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.x.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;


@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageReadBadgeVO {
    @ApiModelProperty(value = "通知接收人用户id")
    private Long targetUid;
    @ApiModelProperty(value = "通知发送人用户id")
    private Long senderUid;
    @ApiModelProperty(value = "提醒消息")
    private String alertMsg;
    @ApiModelProperty(value = "true 有未读消息， 反之false")
    private Boolean hasUnreadMsg;
}
