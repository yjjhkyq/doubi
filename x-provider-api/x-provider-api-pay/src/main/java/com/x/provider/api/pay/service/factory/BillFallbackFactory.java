package com.x.provider.api.pay.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.pay.model.ao.CreateBillAo;
import com.x.provider.api.pay.model.dto.BillDto;
import com.x.provider.api.pay.service.BillRpcService;
import feign.hystrix.FallbackFactory;

public class BillFallbackFactory implements FallbackFactory<BillRpcService> {
    @Override
    public BillRpcService create(Throwable throwable) {
        return new BillRpcService() {

            @Override
            public R<BillDto> createBill(CreateBillAo createBillAo) {
                return null;
            }

            @Override
            public R<BillDto> launchBill(String billSerialNumber) {
                return null;
            }
        };
    }
}
