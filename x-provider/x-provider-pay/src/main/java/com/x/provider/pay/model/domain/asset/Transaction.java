package com.x.provider.pay.model.domain.asset;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import io.swagger.models.auth.In;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("transaction")
public class Transaction extends BaseEntity {
    @TableId
    private Long id;
    private Long fromCustomerId;
    private Long toCustomerId;
    private Long fromCoin;
    private Long fromRice;
    private Integer transactionType;
    private Long toCoin;
    private Long toRice;
    private Long orderId;
}

