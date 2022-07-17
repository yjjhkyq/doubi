package com.x.provider.pay.model.ao;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IncAssetAO {
    private Long customerId;
    private Long coin;
    private Long rice;
}
