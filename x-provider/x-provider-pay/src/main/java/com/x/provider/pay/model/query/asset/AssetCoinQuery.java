package com.x.provider.pay.model.query.asset;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssetCoinQuery {
    private Long customerId;
}
