package com.x.provider.api.pay.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.pay.model.ao.BalanceChangeAo;
import com.x.provider.api.pay.model.ao.CreateWalletAO;
import com.x.provider.api.pay.model.ao.LaunchTransferAo;
import com.x.provider.api.pay.model.dto.BillDto;
import com.x.provider.api.pay.service.WalletRpcService;
import feign.hystrix.FallbackFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class WalletFallbackFactory implements FallbackFactory<WalletRpcService> {
    @Override
    public WalletRpcService create(Throwable throwable) {
        return new WalletRpcService() {
        };
    }
}
