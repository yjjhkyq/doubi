package com.paascloud.provider.oss.controller.frontend;

import com.paascloud.core.utils.ServletUtils;
import com.paascloud.core.web.api.R;
import com.paascloud.provider.oss.service.GreenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController(value = "内容审核")
@RequestMapping("/frontend/green")
public class GreenController {

    private final GreenService greenService;

    public GreenController(@Qualifier("greenService") GreenService greenService){
        this.greenService = greenService;
    }

    @PostMapping("/tencentGreenCallback")
    public R<Void> tencentGreenCallback(@RequestBody Map<String, Object> data) {
        greenService.onGreenResultNotify(data);
        return R.ok();
    }

}
