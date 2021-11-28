package com.x.provider.api.pay.service;

import com.x.core.web.api.R;
import com.x.provider.api.pay.constant.ServiceNameConstants;
import com.x.provider.api.pay.model.ao.CreateBillAo;
import com.x.provider.api.pay.model.dto.BillDto;
import com.x.provider.api.pay.service.factory.BillFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "payService", value = ServiceNameConstants.PAY_SERVICE, fallbackFactory = BillFallbackFactory.class)
public interface BillRpcService {

    @PostMapping(ServiceNameConstants.PAY_RPC_URL_PREFIX_BILL + "/")
    R<BillDto> createBill(@RequestBody CreateBillAo createBillAo);

    @PutMapping(ServiceNameConstants.PAY_RPC_URL_PREFIX_BILL + "/")
    R<BillDto> launchBill(String billSerialNumber);


}
