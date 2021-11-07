package com.x.provider.oss.controller.rpc;

import com.x.core.web.api.R;
import com.x.provider.oss.service.GreenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/notify/oss")
public class NotifyController {

    private final GreenService greenService;

    public NotifyController(GreenService greenService){
        this.greenService = greenService;
    }

    @PostMapping("/green")
    public R<Void> tencentGreenCallback(@RequestBody Map<String, Object> data) {
        greenService.onGreenResultNotify(data);
        return R.ok();
    }

}
