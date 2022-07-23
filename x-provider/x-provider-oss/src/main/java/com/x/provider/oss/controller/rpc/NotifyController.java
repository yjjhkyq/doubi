package com.x.provider.oss.controller.rpc;

import cn.hutool.json.JSONUtil;
import com.tencentcloudapi.vod.v20180717.models.EventContent;
import com.x.core.web.api.R;
import com.x.provider.oss.service.GreenService;
import com.x.provider.oss.service.VodService;
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
    private final VodService vodService;

    public NotifyController(GreenService greenService,
                            VodService vodService){
        this.greenService = greenService;
        this.vodService = vodService;
    }

    @PostMapping("/green")
    public R<Void> tencentGreenCallback(@RequestBody Map<String, Object> data) {
        greenService.onGreenResultNotify(data);
        return R.ok();
    }

    @PostMapping("/vod")
    public void onEvent(@RequestBody Map<String, Object> data){
        String eventContentJsonStr = JSONUtil.toJsonStr(data);
        log.info("on event, data: {}", eventContentJsonStr);
        EventContent eventContent = JSONUtil.toBean(eventContentJsonStr, EventContent.class);
        vodService.onEvent(eventContent);
    }

}
