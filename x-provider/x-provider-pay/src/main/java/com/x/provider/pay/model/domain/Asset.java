package com.x.provider.pay.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("asset")
public class Asset extends BaseEntity {
    @TableId
    private long id;
    private long customerId;
    private int assetType;
    private int amount;
}

