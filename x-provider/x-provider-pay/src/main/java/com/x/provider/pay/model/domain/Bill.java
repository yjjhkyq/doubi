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
@TableName("bill")
public class Bill extends BaseEntity {

    @TableId
    private long id;
    private String serialNumber;
    private long fromCustomerId;
    private long toCustomerId;
    private int amount;
    private int tradeType;
    private int status;
}

