package com.x.provider.pay.model.domain.asset;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("asset_coin")
public class AssetCoin extends BaseEntity {
    @TableId
    private Long id;
    private Long customerId;
    private Long coin;
    private Long costCoin;
    private Long rice;
    private Long withdrawRice;
}

