package com.x.provider.customer.model.ao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class FollowOrUnFollowAO {

    @ApiModelProperty(value = "用户id")
    @NotNull
    private Long toCustomerId;

    @ApiModelProperty(value = "true 关注 反之取消关注")
    @NotNull
    private Boolean follow;
}
