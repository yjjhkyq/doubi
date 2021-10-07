package com.x.provider.customer.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@ApiModel
@Data
public class FlowFansListItemVO {
    @ApiModelProperty(value = "用户id")
    private long customerId;
    @ApiModelProperty(value = "用户属性，NICK_NAME 昵称 AVATAR_ID 头像路径")
    private Map<String, String> customerAttributes;
}
