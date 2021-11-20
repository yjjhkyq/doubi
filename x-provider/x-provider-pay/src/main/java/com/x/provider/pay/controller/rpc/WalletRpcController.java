package com.x.provider.pay.controller.rpc;

import com.x.core.web.api.R;
import com.x.core.web.controller.BaseRpcController;
import com.x.provider.api.pay.model.ao.CreateWalletAO;
import com.x.provider.api.pay.service.WalletRpcService;
import com.x.provider.pay.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rpc/wallet")
public class WalletRpcController extends BaseRpcController implements WalletRpcService {

    @Autowired
    private WalletService walletService;

    @PostMapping("/wallet")
    @Override
    public R<String> createWallet(@RequestBody CreateWalletAO createWalletAO) {
        walletService.createWallet(createWalletAO);
        return R.ok();
    }
}
