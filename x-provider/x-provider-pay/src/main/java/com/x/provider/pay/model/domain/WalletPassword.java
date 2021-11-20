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
@TableName("wallet_password")
public class WalletPassword extends BaseEntity {
    @TableId
    private Long id;
    private Long walletId;
    private Long customerId;
    private String password;
    private String passwordSalt;
}
