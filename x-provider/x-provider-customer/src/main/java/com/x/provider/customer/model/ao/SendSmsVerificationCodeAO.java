package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: liushenyi
 * @date: 2021/11/02/10:35
 */
@ApiModel
@Data
public class SendSmsVerificationCodeAO {
    @ApiModelProperty(name = "电话号码:格式 +8618556565656")
    private String phoneNumber;
}
