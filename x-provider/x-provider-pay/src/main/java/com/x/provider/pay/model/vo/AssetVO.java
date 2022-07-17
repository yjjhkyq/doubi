package com.x.provider.pay.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class AssetVO {
    private Long id;
    @ApiModelProperty(value = "用户id")
    private Long customerId;
    @ApiModelProperty(value = "金币")
    private Long coin;
    @ApiModelProperty(value = "消费金币")
    private Long costCoin;
    @ApiModelProperty(value = "大米")
    private Long rice;
    @ApiModelProperty(value = "提现大米")
    private Long withdrawRice;
}

