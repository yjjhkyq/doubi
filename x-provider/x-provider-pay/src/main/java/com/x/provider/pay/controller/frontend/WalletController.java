package com.x.provider.pay.controller.frontend;

import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.provider.pay.annotation.PayToken;
import com.x.provider.pay.model.ao.ValidateWalletPasswordAO;
import com.x.provider.pay.model.domain.Wallet;
import com.x.provider.pay.service.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "钱包服务")
@RestController
@RequestMapping("/frontend/wallet")
public class WalletController extends BaseFrontendController {

    @Autowired
    private WalletService walletService;

    @ApiOperation(value = "获得钱包余额")
    @GetMapping("/wallet")
    @PayToken
    public R<Wallet> getWallet() {
        Wallet wallet = walletService.getWallet(getCurrentCustomerId());
        return R.ok(wallet);
    }

    // @ApiOperation(value = "转账")
    // @GetMapping("/transfer")
    // public R<Void> transfer() {
    //     walletService.transfer(@RequestBody @ApiParam(value = "转账接受参数", required = true));
    //     return R.ok();
    // }

    @ApiOperation(value = "验证支付密码")
    @PostMapping("/password/validate")
    public R<String> validateWalletPassword(@RequestBody ValidateWalletPasswordAO validateWalletPasswordAO) {
        String token = walletService.validateWalletPassword(getCurrentCustomerId(), validateWalletPasswordAO);
        return R.ok(token);
    }
}
