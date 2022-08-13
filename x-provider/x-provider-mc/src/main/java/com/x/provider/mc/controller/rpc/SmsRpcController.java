package com.x.provider.mc.controller.rpc;

import com.x.core.web.api.R;
import com.x.provider.api.mc.model.dto.SendVerificationCodeDTO;
import com.x.provider.api.mc.model.dto.ValidateVerificationCodeDTO;
import com.x.provider.api.mc.service.SmsRpcService;
import com.x.provider.mc.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rpc/sms")
public class SmsRpcController implements SmsRpcService {

    private final SmsService smsService;
    public SmsRpcController(SmsService smsService){
        this.smsService = smsService;
    }

    @PostMapping("/verification/code/send")
    @Override
    public R<Void> sendVerificationCode( @RequestBody SendVerificationCodeDTO sendVerificationCodeAO) {
        smsService.sendVerificationCode(sendVerificationCodeAO.getPhoneNumber());
        return R.ok();
    }

    @PostMapping("/verification/code/validate")
    @Override
    public R<Void> validateVerificationCode(@RequestBody ValidateVerificationCodeDTO validateVerificationCodeAO) {
        smsService.validateVerificationCode(validateVerificationCodeAO.getPhoneNumber(), validateVerificationCodeAO.getSms());
        return R.ok();
    }
}
