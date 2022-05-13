package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ExternalAuthenticationAO {
    @ApiModelProperty("微信小程序登陆，此处填入code")
    private String oauthAccessToken;
    @ApiModelProperty("微信小程序登陆:1")
    private Integer provider;
}
