package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class BindPhoneAO {

    @ApiModelProperty("绑定的手机号")
    private String phone;

    @ApiModelProperty("短信验证码")
    private String sms;

}
