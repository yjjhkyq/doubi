package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: liushenyi
 * @date: 2021/11/02/11:13
 */
@Data
@ApiModel
public class LoginOrRegBySmsAO {
    @ApiModelProperty(name = "电话号码")
    @NotBlank
    private String phoneNumber;

    @ApiModelProperty(name = "短信验证码")
    @NotBlank
    private String smsVerificationCode;
}
