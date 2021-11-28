package com.x.provider.pay.service;

import com.x.provider.api.pay.model.ao.CreateWalletAO;
import com.x.provider.pay.model.ao.ValidateWalletPasswordAO;
import com.x.provider.pay.model.domain.Bill;
import com.x.provider.pay.model.domain.Wallet;

import java.math.BigDecimal;

public interface WalletService {
    void createWallet(CreateWalletAO createWalletAO);
    Wallet getWallet(long customerId);
    void clearWalletCache(long customerId);
    boolean validateWalletToken(String token, long customerId);

    String validateWalletPassword(long customerId, ValidateWalletPasswordAO validateWalletPasswordAO);

    Bill rechargeWallet(BigDecimal amount, long customerId);

    Bill launchTransfer(BigDecimal amount, Long toCustomerId, long customerId, String comment);

    void receiveTransfer(String billSN, long customerId);

    void updateWallet(Wallet wallet);

    void changeBalance(long customerId, BigDecimal amount);

}
