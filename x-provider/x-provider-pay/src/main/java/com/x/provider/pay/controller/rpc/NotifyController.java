package com.x.provider.pay.controller.rpc;

import com.x.core.utils.JsonUtil;
import com.x.core.web.api.R;
import com.x.provider.api.pay.enums.PayMethodEnum;
import com.x.provider.pay.model.bo.payment.PayNotifyBO;
import com.x.provider.pay.model.bo.payment.PayResultBO;
import com.x.provider.pay.service.checkout.CheckoutService;
import com.x.provider.pay.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/notify/pay")
public class NotifyController {

    private final PaymentService paymentService;
    private final CheckoutService checkoutService;

    public NotifyController(PaymentService paymentService,
                            CheckoutService checkoutService){
        this.paymentService = paymentService;
        this.checkoutService = checkoutService;
    }

    @PostMapping("/wx")
    public R<Void> onWxPayNotify(@RequestBody String body) throws GeneralSecurityException, IOException {
        PayResultBO payResult = paymentService.onPayNotify(PayNotifyBO.builder().payMethod(PayMethodEnum.Wx).body(JsonUtil.parseObject(body, HashMap.class)).build());
        checkoutService.pay(payResult);
        return R.ok();
    }

}
