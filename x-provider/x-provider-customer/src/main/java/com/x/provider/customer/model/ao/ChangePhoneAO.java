package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
@ApiModel
public class ChangePhoneAO {

    @ApiModelProperty("原手机号")
    @NotNull
    private String oldPhone;

    @ApiModelProperty("SMS验证码")
    private String sms;

    @ApiModelProperty("新手机号")
    @NotNull
    private String phone;

}
