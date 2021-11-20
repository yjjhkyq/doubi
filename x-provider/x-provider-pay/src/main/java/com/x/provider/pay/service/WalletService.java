package com.x.provider.pay.service;

import com.x.provider.api.pay.model.ao.CreateWalletAO;
import com.x.provider.pay.model.ao.ValidateWalletPasswordAO;
import com.x.provider.pay.model.domain.Wallet;

public interface WalletService {
    void createWallet(CreateWalletAO createWalletAO);
    Wallet getWallet(long customerId);
    boolean validateWalletToken(String token, long customerId);

    String validateWalletPassword(long customerId, ValidateWalletPasswordAO validateWalletPasswordAO);
}
