package com.x.provider.api.pay.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.pay.model.ao.CreateWalletAO;
import com.x.provider.api.pay.service.WalletRpcService;
import feign.hystrix.FallbackFactory;

import java.util.List;
import java.util.Map;

public class WalletFallbackFactory implements FallbackFactory<WalletRpcService> {
    @Override
    public WalletRpcService create(Throwable throwable) {
        return new WalletRpcService() {
            @Override
            public R<String> createWallet(CreateWalletAO createWalletAO) {
                return null;
            }
        };
    }
}
