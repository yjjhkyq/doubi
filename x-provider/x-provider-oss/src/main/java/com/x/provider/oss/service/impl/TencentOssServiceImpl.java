package com.x.provider.oss.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.x.provider.oss.configure.TencentOssConfig;
import com.x.provider.oss.model.vo.oss.TencentOssCredentialVO;
import com.x.provider.oss.service.OssService;
import com.x.provider.oss.service.RedisKeyService;
import com.x.provider.oss.service.TencentOssService;
import com.x.redis.service.RedisService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.AnonymousCOSCredentials;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.tencent.cloud.CosStsClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
public class TencentOssServiceImpl implements TencentOssService {

    private final int OSS_TEMP_SECRET_EXP_TIME = 1800;
    private final TencentOssConfig tencentOssConfig;
    private final RedisService redisService;
    private final RedisKeyService redisKeyService;

    public TencentOssServiceImpl(TencentOssConfig tencentOssConfig,
                                 RedisService redisService,
                                 RedisKeyService redisKeyService){
        this.tencentOssConfig = tencentOssConfig;
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
    }

    @Override
    public TencentOssCredentialVO getTencentOssUploadCredentia(long customerId, String extName){
        String fileName = StrUtil.format("{}.{}", IdUtil.simpleUUID(), extName);
        String objectKey = OssService.getObjectKey(customerId, fileName);
        TencentOssCredentialVO result = getCredential(tencentOssConfig.getOssBucketCustomer(), TencentOssConfig.AP_CHENGDU, objectKey);
        result.setFileName(fileName);
        return result;
    }

    @Override
    public void upload(TencentOssCredentialVO tencentOssCredentialVO, InputStream inputStream){

        final COSClient cosclient = getTempCosClient(tencentOssCredentialVO);
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(tencentOssCredentialVO.getBucketName(),
                    tencentOssCredentialVO.getAllowPrefix(), inputStream, null);
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
        } catch (CosServiceException e) {
            //??????????????? CosServiceException
            e.printStackTrace();
        } catch (CosClientException e) {
            //??????????????? CosClientException
            e.printStackTrace();
        }
        // ???????????????
        cosclient.shutdown();
    }

    private COSClient getTempCosClient(TencentOssCredentialVO tencentOssCredential){
        BasicSessionCredentials cred = new BasicSessionCredentials(tencentOssCredential.getTmpSecretId(),
                tencentOssCredential.getTmpSecretKey(), tencentOssCredential.getSessionToken());
        Region region = new Region(tencentOssCredential.getRegionName());
        ClientConfig clientConfig = new ClientConfig(region);
        return new COSClient(cred, clientConfig);
    }

    private COSClient getCosClient(){
        COSCredentials cred = new BasicCOSCredentials(tencentOssConfig.getAppSecretId(), tencentOssConfig.getAppSecretKey());
        // 2 ?????? bucket ??????,??????????????? COS ?????? https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(TencentOssConfig.AP_CHENGDU));
        // 3 ?????? cos ?????????
        return new COSClient(cred, clientConfig);
    }


    public TencentOssCredentialVO getCredential(String bucket, String region, String allowPrefix){
        TreeMap<String, Object> config = new TreeMap<>();
        try {
            // ??????????????? SecretId
            config.put("SecretId", tencentOssConfig.getAppSecretId());
            // ??????????????? SecretKey
            config.put("SecretKey", tencentOssConfig.getAppSecretKey());

            // ????????????????????????????????????????????????1800???????????????????????????2????????????7200????????????????????????36????????????129600??????
            config.put("durationSeconds", OSS_TEMP_SECRET_EXP_TIME);

            // ???????????? bucket
            config.put("bucket", bucket);
            // ?????? bucket ????????????
            config.put("region", region);

            // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????a.jpg ?????? a/* ?????? * ???
            // ??????????????????*??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            config.put("allowPrefix", allowPrefix);

            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????? https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[] {
                    // ????????????
                    "name/cos:PutObject",
                    // ??????????????????????????????
                    "name/cos:PostObject",
                    // ????????????
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);

            JSONObject result = CosStsClient.getCredential(config);
            JSONObject credentials = result.getJSONObject("credentials");
            TencentOssCredentialVO tencentOssCredentialVO = new TencentOssCredentialVO();
            tencentOssCredentialVO.setSessionToken(credentials.getString("sessionToken"));
            tencentOssCredentialVO.setTmpSecretId(credentials.getString("tmpSecretId"));
            tencentOssCredentialVO.setTmpSecretKey(credentials.getString("tmpSecretKey"));
            tencentOssCredentialVO.setBucketName(bucket);
            tencentOssCredentialVO.setAllowPrefix(allowPrefix);
            tencentOssCredentialVO.setRegionName(region);
            tencentOssCredentialVO.setExpiredTime(System.currentTimeMillis() / 1000 + OSS_TEMP_SECRET_EXP_TIME);
            return tencentOssCredentialVO;
        } catch (Exception e) {
            throw new IllegalArgumentException("no valid secret !");
        }
    }

    public String getOjectBrowseUrl(String key){
        return getObjectAnonymousAccessUrl(tencentOssConfig.getOssBucketCustomer(), Arrays.asList(key)).get(key);
    }

    @Override
    public Map<String, String> listOjectBrowseUrl(List<String> objectKeys) {
        return getObjectAnonymousAccessUrl(tencentOssConfig.getOssBucketCustomer(), objectKeys);
    }


    public Map<String, String> getObjectAnonymousAccessUrl(String bucketName, List<String> keys){
        Map<String, String> result = new HashMap<>(keys.size());
        COSCredentials cred = new AnonymousCOSCredentials();
        ClientConfig clientConfig = new ClientConfig(new Region(TencentOssConfig.AP_CHENGDU));
        COSClient cosClient = new COSClient(cred, clientConfig);
        try {
            keys.forEach(item -> {
                GeneratePresignedUrlRequest req =
                        new GeneratePresignedUrlRequest(bucketName, item, HttpMethodName.GET);
                result.put(item, cosClient.generatePresignedUrl(req).toString());
            });
        } finally {
            cosClient.shutdown();
        }
        return result;
    }
}
