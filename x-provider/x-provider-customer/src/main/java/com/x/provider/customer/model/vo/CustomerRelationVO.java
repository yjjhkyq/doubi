package com.x.provider.customer.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CustomerRelationVO {
    private Long id;

    private Long fromCustomerId;

    private Long toCustomerId;

    @ApiModelProperty(value = "true 关注关系 反之false")
    private Boolean follow;

    @ApiModelProperty(value = "true 朋友关系 反之false")
    private Boolean friend;
}
