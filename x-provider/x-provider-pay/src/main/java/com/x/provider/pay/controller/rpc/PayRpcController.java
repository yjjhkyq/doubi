package com.x.provider.pay.controller.rpc;

import com.x.core.web.api.R;
import com.x.core.web.controller.BaseRpcController;
import com.x.provider.api.pay.model.ao.TransactionAo;
import com.x.provider.api.pay.model.dto.TransactionDTO;
import com.x.provider.api.pay.service.PayRpcService;
import com.x.provider.pay.service.PayService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rpc/pay")
public class PayRpcController extends BaseRpcController implements PayRpcService {

    private final PayService payService;

    public PayRpcController(PayService payService){
        this.payService = payService;
    }

    @PostMapping("/transaction")
    @Override
    public R<TransactionDTO> transaction(TransactionAo transaction) {
        return R.ok(payService.transaction(transaction));
    }

    @PostMapping("/asset/init")
    @Override
    public R<Void> initAsset(Long customerId) {
        payService.initAsset(customerId);
        return R.ok();
    }

}
