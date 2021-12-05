package com.x.provider.vod.controller.frontend;

import com.x.core.web.api.R;
import com.x.core.web.controller.BaseController;
import com.x.provider.vod.model.vo.VodUploadParamVO;
import com.x.provider.vod.service.VodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Api(tags = "点播服务")
@RestController
@RequestMapping("/frontend/vod")
public class VodController extends BaseController {

    private final VodService vodService;

    public VodController(VodService vodService){
        this.vodService = vodService;
    }

    @ApiOperation(value = "获取上传短视频参数")
    @PostMapping("/upload/param")
    public R<VodUploadParamVO> getVodUploadParam(@RequestParam @NotBlank @ApiParam(value = "上传对象扩展名") String extName){
        return R.ok(vodService.getVodUploadParam(getCurrentCustomerId(), extName));
    }
}
