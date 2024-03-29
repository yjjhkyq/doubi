package com.x.provider.oss.controller.app;

import cn.hutool.core.io.FileUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.provider.oss.model.vo.oss.TencentOssCredentialVO;
import com.x.provider.oss.service.OssService;
import com.x.provider.oss.service.TencentOssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Api(tags = "对象存储服务")
@RestController
@RequestMapping("/app/oss")
public class OssController extends BaseFrontendController {

    private final OssService ossService;
    private final TencentOssService tencentOssService;

    public OssController(OssService ossService,
                         TencentOssService tencentOssService){
        this.ossService = ossService;
        this.tencentOssService = tencentOssService;
    }

    @ApiOperation(value = "获取对象访问url")
    @GetMapping("/browser/url")
    public R<String> getOjectBrowseUrl(@RequestParam @ApiParam(value = "对象key") String objectKey){
        return R.ok(ossService.getObjectBrowseUrl(objectKey));
    }

    @ApiOperation(value = "获取对象上传参数")
    @GetMapping("/tencent/upload/credential")
    public R<TencentOssCredentialVO> getTencentOssUploadCredentia(@RequestParam @NotBlank @ApiParam(value = "上传文件名") String fileName){
        return R.ok(tencentOssService.getTencentOssUploadCredentia(getCurrentCustomerId(), FileUtil.extName(fileName)));
    }

}
