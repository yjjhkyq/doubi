package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ValidatePhoneAO {

  @ApiModelProperty("手机号码")
  private String phone;

}
