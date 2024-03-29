package com.x.provider.oss.service;

import com.x.provider.oss.model.vo.oss.TencentOssCredentialVO;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface TencentOssService {
    TencentOssCredentialVO getTencentOssUploadCredentia(long customerId, String extName);
    String getObjectBrowseUrl(String key);
    Map<String, String> listObjectBrowseUrl(List<String> objectKeys);
    void upload(TencentOssCredentialVO tencentOssCredentialVO, InputStream inputStream);
}
