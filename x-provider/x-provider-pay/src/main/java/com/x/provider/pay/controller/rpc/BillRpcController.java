package com.x.provider.pay.controller.rpc;

import com.x.core.web.api.R;
import com.x.core.web.controller.BaseRpcController;
import com.x.provider.api.pay.model.ao.CreateBillAo;
import com.x.provider.api.pay.model.dto.BillDto;
import com.x.provider.api.pay.service.BillRpcService;
import com.x.provider.pay.model.domain.Bill;
import com.x.provider.pay.service.BillService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rpc/bill")
public class BillRpcController extends BaseRpcController implements BillRpcService {

    @Autowired
    private BillService billService;

    @PostMapping("/")
    @Override
    public R<BillDto> createBill(CreateBillAo createBillAo) {
        Bill bill = billService.createBill(createBillAo);
        BillDto billDto = new BillDto();
        BeanUtils.copyProperties(bill, billDto);
        return R.ok(billDto);
    }

    @PutMapping("/")
    @Override
    public R<BillDto> launchBill(String billSerialNumber) {
        Bill bill = billService.launchBill(billSerialNumber);
        BillDto billDto = new BillDto();
        BeanUtils.copyProperties(bill, billDto);
        return R.ok(billDto);
    }
}
