package com.x.provider.api.pay.service;


import com.x.core.web.api.R;
import com.x.provider.api.pay.constant.ServiceNameConstants;
import com.x.provider.api.pay.model.dto.CreateTransactionDTO;
import com.x.provider.api.pay.model.dto.TransactionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "payService", value = ServiceNameConstants.PAY_SERVICE)
public interface PayRpcService {
    @PostMapping(ServiceNameConstants.PAY_RPC_URL_PREFIX_PAY + "/transaction")
    R<TransactionDTO> transaction(@RequestBody CreateTransactionDTO transaction);

    @PostMapping(ServiceNameConstants.PAY_RPC_URL_PREFIX_PAY + "/asset/init")
    R<Void> initAsset(Long customerId);
}
