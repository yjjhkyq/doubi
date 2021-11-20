package com.x.provider.api.pay.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletAO {
    private Long customerId;
    private String phone;
    private String walletPassword;
}
