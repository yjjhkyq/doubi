package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class ChangePasswordByOldPasswordAO {
    @ApiModelProperty(name = "旧密码")
    @NotBlank
    private String oldPassword;

    @ApiModelProperty(name = "新密码")
    @NotBlank
    private String newPassword;
}
