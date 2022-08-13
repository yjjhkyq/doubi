package com.x.provider.pay.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class CreateOrderResultVO {
    @ApiModelProperty(value = "授权交易码，对于微信支付，此值为prepay_id")
    private String authorizationTransactionCode;
    @ApiModelProperty(value = "订单好", required = true)
    private String orderNo;
}
