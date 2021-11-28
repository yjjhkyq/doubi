package com.x.provider.pay.service;

public interface RedisKeyService {

    String getWalletKey(long customerId);

    String getWalletPasswordKey(long customerId);

    String getWalletPasswordInfoKey(String token);

    String getWalletPasswordValidateTimesKey(long customerId);

    String getBillKey(String billSN);

    String getBillKey(Long billId);
}
