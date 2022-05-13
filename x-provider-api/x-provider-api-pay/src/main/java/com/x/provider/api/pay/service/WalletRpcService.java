package com.x.provider.api.pay.service;

import com.x.core.web.api.R;
import com.x.provider.api.pay.constant.ServiceNameConstants;
import com.x.provider.api.pay.model.ao.CreateWalletAO;
import com.x.provider.api.pay.model.ao.BalanceChangeAo;
import com.x.provider.api.pay.model.ao.LaunchTransferAo;
import com.x.provider.api.pay.model.dto.BillDto;
import com.x.provider.api.pay.service.factory.WalletFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(contextId = "payService", value = ServiceNameConstants.PAY_SERVICE, fallbackFactory = WalletFallbackFactory.class)
public interface WalletRpcService {

}
