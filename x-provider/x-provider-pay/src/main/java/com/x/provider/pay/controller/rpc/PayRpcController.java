package com.x.provider.pay.controller.rpc;

import com.x.core.web.api.R;
import com.x.core.web.controller.BaseRpcController;
import com.x.provider.api.pay.model.dto.CreateTransactionDTO;
import com.x.provider.api.pay.model.dto.TransactionDTO;
import com.x.provider.api.pay.service.PayRpcService;
import com.x.provider.pay.service.asset.AssetCoinService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rpc/pay")
public class PayRpcController extends BaseRpcController implements PayRpcService {

    private final AssetCoinService assetService;

    public PayRpcController(AssetCoinService assetService){
        this.assetService = assetService;
    }

    @PostMapping("/transaction")
    @Override
    public R<TransactionDTO> transaction(CreateTransactionDTO transaction) {
        return R.ok(TransactionDTO.builder().id(assetService.transaction(transaction)).build());
    }

    @PostMapping("/asset/init")
    @Override
    public R<Void> initAsset(Long customerId) {
        assetService.initAsset(customerId);
        return R.ok();
    }

}
