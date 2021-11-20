package com.x.provider.pay.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ValidateWalletPasswordAO {

    @ApiModelProperty("密码")
    private String password;
}
