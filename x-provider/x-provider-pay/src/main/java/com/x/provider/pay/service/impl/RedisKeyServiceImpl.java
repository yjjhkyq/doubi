package com.x.provider.pay.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.provider.pay.service.RedisKeyService;
import org.springframework.stereotype.Service;

@Service
public class RedisKeyServiceImpl implements RedisKeyService {

    private static final String KEY_PREFIX = "Pay";


    private static final String WALLET_PREFIX = "Wallet:";
    private static final String WALLET_PASSWORD_PREFIX = "Wallet:Password:";
    private static final String BILL_PREFIX = "Wallet:Bill:";

    private static final String FULL_KEY = KEY_PREFIX + ":{}";

    /**
     * Pay:Wallet:Customer:Id:{}
     */
    private static final String WALLET_BY_CUSTOMER_ID = WALLET_PREFIX + "Customer:Id:{}";
    private static final String WALLET_PASSWORD_BY_CUSTOMER_ID = WALLET_PASSWORD_PREFIX + "Customer:Id:{}";

    private static final String WALLET_PASSWORD_INFO_BY_TOKEN = WALLET_PASSWORD_PREFIX + "Info:Token:{}";
    private static final String WALLET_PASSWORD_VALIDATE_TIMES = WALLET_PASSWORD_PREFIX + "Validate:Times:Customer:Id:{}";

    private static final String BILL_BY_BILL_SN = BILL_PREFIX + "Bill:Serial:Number:{}";
    private static final String BILL_BY_BILL_ID = BILL_PREFIX + "Bill:Id:{}";


    @Override
    public String getWalletKey(long customerId) {
        return getFullKey(WALLET_BY_CUSTOMER_ID, customerId);
    }

    @Override
    public String getWalletPasswordKey(long customerId) {
        return getFullKey(WALLET_PASSWORD_BY_CUSTOMER_ID, customerId);
    }

    @Override
    public String getWalletPasswordInfoKey(String token) {
        return getFullKey(WALLET_PASSWORD_INFO_BY_TOKEN, token);
    }

    @Override
    public String getWalletPasswordValidateTimesKey(long customerId) {
        return getFullKey(WALLET_PASSWORD_VALIDATE_TIMES, customerId);
    }

    @Override
    public String getBillKey(String billSN) {
        return getFullKey(BILL_BY_BILL_SN, billSN);
    }

    @Override
    public String getBillKey(Long billId) {
        return getFullKey(BILL_BY_BILL_ID, billId);
    }

    private String getFullKey(CharSequence keyTemplate, Object... params) {
        return StrUtil.format(FULL_KEY, StrUtil.format(keyTemplate, params));
    }
}
