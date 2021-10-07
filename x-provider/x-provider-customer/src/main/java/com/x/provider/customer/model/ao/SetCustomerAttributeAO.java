package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel
@Data
public class SetCustomerAttributeAO {

    @ApiModelProperty(value = "属性名,NICK_NAME 昵称 AVATAR_ID 头像文件ID PERSONAL_HOMEPAGE_BACKGROUND_ID 个人主页背景图片 SIGNATURE 签名")
    @NotBlank
    private String attributeName;

    @ApiModelProperty(value = "属性值")
    @NotBlank
    private String value;
}
