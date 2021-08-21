package com.paascloud.provider.oss.controller.frontend;

import com.paascloud.core.web.api.R;
import com.paascloud.core.web.controller.BaseFrontendController;
import com.paascloud.provider.oss.model.vo.TencentOssCredentialVO;
import com.paascloud.provider.oss.service.OssService;
import com.paascloud.provider.oss.service.TencentOssService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
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
