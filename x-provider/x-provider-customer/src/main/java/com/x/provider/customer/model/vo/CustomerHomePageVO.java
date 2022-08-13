package com.x.provider.customer.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class CustomerHomePageVO {
    @ApiModelProperty(value = "用户id")
    private long id;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "关注数")
    private long followCount;
    @ApiModelProperty(value = "粉丝数")
    private long fansCount;
    @ApiModelProperty(value = "用户统计信息")
    private CustomerStatVO statistic;
    @ApiModelProperty(value = "0 没有关系 1 关注关系 2 朋友关系 3 粉丝关系 ")
    Integer customerRelation;
    @ApiModelProperty(value = "true 能够关注 反之false")
    private Boolean canFollow = false;
    @ApiModelProperty(value = "用户属性，NICK_NAME 昵称 AVATAR_ID 头像 PERSONAL_HOMEPAGE_BACKGROUND_ID 个人主页背景图片 SIGNATURE 签名, GENDER 1 男 2 女")
    private List<GenericAttributeVO> attributes;

}
