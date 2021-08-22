package com.x.provider.oss.controller.frontend;

import com.x.core.web.api.R;
import com.x.core.web.controller.BaseFrontendController;
import com.x.provider.oss.model.vo.TencentOssCredentialVO;
import com.x.provider.oss.service.OssService;
import com.x.provider.oss.service.TencentOssService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/frontend/oss")
public class OssController extends BaseFrontendController {

    private final OssService ossService;
    private final TencentOssService tencentOssService;

    public OssController(OssService ossService,
                         TencentOssService tencentOssService){
        this.ossService = ossService;
        this.tencentOssService = tencentOssService;
    }

    @GetMapping("/getOjectBrowseUrl")
    public R<String> getOjectBrowseUrl(@RequestParam String objectKey){
        return R.ok(ossService.getOjectBrowseUrl(objectKey));
    }

    @GetMapping("/getTencentOssUploadCredentia")
    public R<TencentOssCredentialVO> getTencentOssUploadCredentia(@RequestParam @NotBlank String extName){
        return R.ok(tencentOssService.getTencentOssUploadCredentia(getCurrentCustomerId(), extName));
    }

}
