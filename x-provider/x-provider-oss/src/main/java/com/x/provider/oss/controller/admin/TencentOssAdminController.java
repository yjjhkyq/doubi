package com.x.provider.oss.controller.admin;

import cn.hutool.core.io.FileUtil;
import com.x.core.web.api.R;
import com.x.core.web.controller.BaseAdminController;
import com.x.provider.oss.model.vo.TencentOssCredentialVO;
import com.x.provider.oss.service.TencentOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController(value = "腾讯云对象存储管理端API")
@RequestMapping("/admin/tencentoss")
public class TencentOssAdminController extends BaseAdminController {

    private final TencentOssService tencentOssService;

    public TencentOssAdminController(TencentOssService tencentOssService){
        this.tencentOssService = tencentOssService;
    }

    @PostMapping("/testUpload")
    public R<Void> upload(MultipartFile multipartFile) throws IOException {
        TencentOssCredentialVO tencentOssUploadCredentia = tencentOssService.getTencentOssUploadCredentia(getCurrentCustomerId(), FileUtil.extName(multipartFile.getOriginalFilename()));
        tencentOssService.upload(tencentOssUploadCredentia, multipartFile.getInputStream());
        return R.ok();
    }
}
