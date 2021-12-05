package com.x.provider.general.controller.admin;

import com.x.provider.general.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController(value = "通用业务管理端")
@RequestMapping("/admin/general")
public class GeneralAdminController {

    private final SmsService smsService;

    public GeneralAdminController(SmsService smsService) {
        this.smsService = smsService;
    }
}
