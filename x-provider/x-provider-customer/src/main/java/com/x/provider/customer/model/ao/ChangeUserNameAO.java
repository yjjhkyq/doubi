package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ChangeUserNameAO {
    @ApiModelProperty(name = "用户名")
    private String userName;
}
