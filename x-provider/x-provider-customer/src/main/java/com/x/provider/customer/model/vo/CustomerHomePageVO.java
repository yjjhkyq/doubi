package com.x.provider.customer.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@ApiModel
@Data
public class CustomerHomePageVO {
    @ApiModelProperty(value = "用户id")
    private long id;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "用户属性，NICK_NAME 昵称 AVATAR_ID 头像 PERSONAL_HOMEPAGE_BACKGROUND_ID 个人主页背景图片 SIGNATURE 签名")
    private Map<String, String> attributes;
    @ApiModelProperty(value = "关注数")
    private long followCount;
    @ApiModelProperty(value = "粉丝数")
    private long fansCount;
}
