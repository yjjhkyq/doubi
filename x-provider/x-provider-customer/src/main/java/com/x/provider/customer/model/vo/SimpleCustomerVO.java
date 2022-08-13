package com.x.provider.customer.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class SimpleCustomerVO {
    @ApiModelProperty(value = "用户id")
    private Long id;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "昵称")
    private String nickName;
    @ApiModelProperty(value = "头像")
    private String avatarUrl;
    @ApiModelProperty(value = "性别 0 未知 ")
    private Integer gender;

    @ApiModelProperty(value = "true 能够关注 反之false")
    private Boolean canFollow = false;

    @ApiModelProperty(value = "0 没有关系 1 关注关系 2 朋友关系 3 粉丝关系")
    private Integer customerRelation;
}

