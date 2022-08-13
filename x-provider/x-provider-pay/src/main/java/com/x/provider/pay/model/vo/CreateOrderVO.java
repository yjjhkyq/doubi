package com.x.provider.pay.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderVO {
    @NotNull
    @Min(1)
    @ApiModelProperty(value = "商品id", required = true)
    private Long productId;

    @NotNull
    @Min(1)
    @ApiModelProperty(value = "支付方式 1 微信", required = true)
    private Integer payMethod;
}
