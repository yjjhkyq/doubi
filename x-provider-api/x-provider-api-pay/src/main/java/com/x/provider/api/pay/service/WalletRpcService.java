package com.x.provider.api.pay.service;

import com.x.core.web.api.R;
import com.x.provider.api.pay.constant.ServiceNameConstants;
import com.x.provider.api.pay.model.ao.CreateWalletAO;
import com.x.provider.api.pay.service.factory.WalletFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "payService", value = ServiceNameConstants.PAY_SERVICE, fallbackFactory = WalletFallbackFactory.class)
public interface WalletRpcService {

    @PostMapping(ServiceNameConstants.PAY_RPC_URL_PREFIX_WALLET + "/wallet")
    R<String> createWallet(@RequestBody CreateWalletAO createWalletAO);
}
