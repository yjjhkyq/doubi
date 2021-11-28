package com.x.provider.mc.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSenderVO {
    @ApiModelProperty(value = "发送人用户id")
    private Long senderUid;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "头像")
    private String avatarUrl;

    @ApiModelProperty(value = "true 即时消息， false 站内信， 对于站内信的话需要调用服务端的接口分页拉取消息")
    private boolean im;
}
