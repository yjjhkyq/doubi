package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class ValidatePhoneAO {

  @ApiModelProperty("手机号码")
  @NotEmpty
  private String phone;

}
