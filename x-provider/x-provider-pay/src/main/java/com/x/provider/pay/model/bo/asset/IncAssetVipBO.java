package com.x.provider.pay.model.bo.asset;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IncAssetVipBO {
    private Long customerId;
    private Integer durationDay;
}
