package com.x.provider.pay.model.domain.asset;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("asset_vip")
public class AssetVip extends BaseEntity {
    @TableId
    private Long id;
    private Long customerId;
    private Long productId;
    private Date expireDate;
}

