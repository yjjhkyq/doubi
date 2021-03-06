package com.x.provider.vod.controller.rpc;

import cn.hutool.json.JSONUtil;
import com.tencentcloudapi.vod.v20180717.models.EventContent;
import com.x.provider.vod.service.VodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/notify/vod")
public class NotifyController {

    private final VodService vodService;

    public NotifyController(VodService vodService){
        this.vodService = vodService;
    }

    @PostMapping("/tencent")
    public void onEvent(@RequestBody Map<String, Object> data){
        String eventContentJsonStr = JSONUtil.toJsonStr(data);
        log.info("on event, data: {}", eventContentJsonStr);
        EventContent eventContent = JSONUtil.toBean(eventContentJsonStr, EventContent.class);
        vodService.onEvent(eventContent);
    }

}
