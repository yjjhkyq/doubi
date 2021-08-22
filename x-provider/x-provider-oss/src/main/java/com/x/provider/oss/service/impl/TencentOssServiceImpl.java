package com.x.provider.oss.service.impl;

import com.x.provider.oss.configure.TencentOssConfig;
import com.x.provider.oss.model.vo.TencentOssCredentialVO;
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
        return getCredential(tencentOssConfig.getBucketCustomer(), TencentOssConfig.AP_CHENGDU, OssService.getObjectKey(customerId, extName));
    }

    @Override
    public void upload(TencentOssCredentialVO tencentOssCredentialVO, InputStream inputStream){

        final COSClient cosclient = getTempCosClient(tencentOssCredentialVO);
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(tencentOssCredentialVO.getBucketName(),
                    tencentOssCredentialVO.getAllowPrefix(), inputStream, null);
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
        } catch (CosServiceException e) {
            //失败，抛出 CosServiceException
            e.printStackTrace();
        } catch (CosClientException e) {
            //失败，抛出 CosClientException
            e.printStackTrace();
        }
        // 关闭客户端
        cosclient.shutdown();
    }

    private COSClient getTempCosClient(TencentOssCredentialVO tencentOssCredential){
        String tmpSecretId = "COS_SECRETID";
        String tmpSecretKey = "COS_SECRETKEY";
        String sessionToken = "COS_TOKEN";
        BasicSessionCredentials cred = new BasicSessionCredentials(tencentOssCredential.getTmpSecretId(),
                tencentOssCredential.getTmpSecretKey(), tencentOssCredential.getSessionToken());
        Region region = new Region(tencentOssCredential.getRegionName());
        ClientConfig clientConfig = new ClientConfig(region);
        return new COSClient(cred, clientConfig);
    }

    private COSClient getCosClient(){
        COSCredentials cred = new BasicCOSCredentials(tencentOssConfig.getSecretId(), tencentOssConfig.getSecretKey());
        // 2 设置 bucket 区域,详情请参阅 COS 地域 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(TencentOssConfig.AP_CHENGDU));
        // 3 生成 cos 客户端
        return new COSClient(cred, clientConfig);
    }


    public TencentOssCredentialVO getCredential(String bucket, String region, String allowPrefix){
        TreeMap<String, Object> config = new TreeMap<>();
        try {
            // 替换为您的 SecretId
            config.put("SecretId", tencentOssConfig.getSecretId());
            // 替换为您的 SecretKey
            config.put("SecretKey", tencentOssConfig.getSecretKey());

            // 临时密钥有效时长，单位是秒，默认1800秒，目前主账号最长2小时（即7200秒），子账号最长36小时（即129600秒）
            config.put("durationSeconds", 1800);

            // 换成您的 bucket
            config.put("bucket", bucket);
            // 换成 bucket 所在地区
            config.put("region", region);

            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的具体路径，例子：a.jpg 或者 a/* 或者 * 。
            // 如果填写了“*”，将允许用户访问所有资源；除非业务需要，否则请按照最小权限原则授予用户相应的访问权限范围。
            config.put("allowPrefix", allowPrefix);

            // 密钥的权限列表。简单上传、表单上传和分片上传需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[] {
                    // 简单上传
                    "name/cos:PutObject",
                    // 表单上传、小程序上传
                    "name/cos:PostObject",
                    // 分片上传
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
            return tencentOssCredentialVO;
        } catch (Exception e) {
            throw new IllegalArgumentException("no valid secret !");
        }
    }

    public String getOjectBrowseUrl(String key){
        return getObjectAnonymousAccessUrl(tencentOssConfig.getBucketCustomer(), Arrays.asList(key)).get(key);
    }

    @Override
    public Map<String, String> listOjectBrowseUrl(List<String> objectKeys) {
        return getObjectAnonymousAccessUrl(tencentOssConfig.getBucketCustomer(), objectKeys);
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
