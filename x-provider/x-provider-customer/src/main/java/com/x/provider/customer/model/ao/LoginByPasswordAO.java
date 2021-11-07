package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class LoginByPasswordAO {
    @ApiModelProperty(name = "用户名,根据用户名登录时填入此值")
    private String userName;

    @ApiModelProperty(name = "手机号码：例如+8618222233232, 根据手机号码进行登录时填入此值")
    private String phone;

    @ApiModelProperty(name = "密码")
    @NotBlank
    private String password;
}
