package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class UserNamePasswordRegisterAO {
    @NotBlank
    @ApiModelProperty(name = "用户名")
    private String userName;

    @NotBlank
    @ApiModelProperty(name = "密码")
    private String password;
}
