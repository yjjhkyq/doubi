package com.x.provider.pay.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("wallet")
public class Wallet extends BaseEntity {
    @TableId
    private long id;
    private long customerId;
    private BigDecimal balance;
    @TableField("is_active")
    private boolean active;
    @TableField("is_locked")
    private boolean locked;

}
