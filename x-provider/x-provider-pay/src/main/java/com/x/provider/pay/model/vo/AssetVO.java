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
    @ApiModelProperty(value = "金币资产")
    private AssetCoinVO assetCoin;

    @ApiModelProperty(value = "vip资产")
    private AssetVipVO assetVip;
}

