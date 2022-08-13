package com.x.provider.pay.controller.admin;

import com.x.core.web.api.R;
import com.x.provider.pay.model.bo.payment.PayResultBO;
import com.x.provider.pay.service.checkout.CheckoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/pay")
public class AdminPayController {

    private final CheckoutService checkoutService;

    public AdminPayController(CheckoutService checkoutService){
        this.checkoutService = checkoutService;
    }

    @PostMapping("/notify/mock")
    public R<Void> onWxPayNotify(@RequestBody PayResultBO payResult) {
        checkoutService.pay(payResult);
        return R.ok();
    }
}
