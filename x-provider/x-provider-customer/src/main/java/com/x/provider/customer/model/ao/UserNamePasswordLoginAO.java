package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class UserNamePasswordLoginAO {
    @ApiModelProperty(name = "用户名")
    @NotBlank
    private String userName;

    @ApiModelProperty(name = "密码")
    @NotBlank
    private String password;
}
