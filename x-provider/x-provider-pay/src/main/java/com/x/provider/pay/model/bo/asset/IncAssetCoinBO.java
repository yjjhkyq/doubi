package com.x.provider.pay.model.bo.asset;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IncAssetCoinBO {
    private Long customerId;
    private Long coin;
    private Long rice;
    private Long costCoin;
}
