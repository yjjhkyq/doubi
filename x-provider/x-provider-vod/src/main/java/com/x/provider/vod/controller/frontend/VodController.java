package com.x.provider.vod.controller.frontend;

import com.x.core.web.api.R;
import com.x.core.web.controller.BaseController;
import com.x.provider.vod.model.vo.VodUploadParamVO;
import com.x.provider.vod.service.VodService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/vod")
public class VodController extends BaseController {

    private final VodService vodService;

    public VodController(VodService vodService){
        this.vodService = vodService;
    }

    @PostMapping("/upload/param")
    public R<VodUploadParamVO> getVodUploadParam(){
        return R.ok(vodService.getVodUploadParam(getCurrentCustomerId()));
    }
}
