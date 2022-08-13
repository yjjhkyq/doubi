package com.x.provider.pay.service.payment;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.x.core.exception.ApiException;
import com.x.core.utils.JsonUtil;
import com.x.provider.api.pay.enums.PayMethodEnum;
import com.x.provider.api.pay.enums.PaymentStatusEnum;
import com.x.provider.pay.configure.TencentConfig;
import com.x.provider.pay.model.bo.payment.CreateOrderResultBO;
import com.x.provider.pay.model.bo.payment.PayNotifyBO;
import com.x.provider.pay.model.bo.payment.PayResultBO;
import com.x.provider.pay.model.bo.payment.QueryOrderResultBO;
import com.x.provider.pay.model.domain.order.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("wxPaymentMethod")
public class WxPaymentMethod implements PaymentMethod {

    private final TencentConfig tencentConfig;
    private CloseableHttpClient httpClient;


    public WxPaymentMethod(TencentConfig tencentConfig){
        this.tencentConfig = tencentConfig;
    }

    @PostConstruct
    public void setup() throws IOException {
        // 加载商户私钥（privateKey：私钥字符串）
//        PrivateKey merchantPrivateKey = PemUtil
//                .loadPrivateKey(new ByteArrayInputStream(tencentConfig.getPrivateKey().getBytes("utf-8")));
//
//        // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
//        AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
//                new WechatPay2Credentials(tencentConfig.getMchid(), new PrivateKeySigner(tencentConfig.getMchSerialNo(), merchantPrivateKey)), tencentConfig.getApiKey().getBytes("utf-8"));
//
//        // 初始化httpClient
//        httpClient = WechatPayHttpClientBuilder.create()
//                .withMerchant(tencentConfig.getMchid(), tencentConfig.getMchSerialNo(), merchantPrivateKey)
//                .withValidator(new WechatPay2Validator(verifier)).build();
    }

    @PreDestroy
    public void after() {
        try {
            httpClient.close();
        }
        catch (Exception e){

        }
    }

    @Override
    public PayResultBO onPayNotify(PayNotifyBO payNotifyAO) throws GeneralSecurityException, IOException {
        if (!"TRANSACTION.SUCCESS".equals(payNotifyAO.getBody().get("event_type").toString()) && !"encrypt-resource".equals(payNotifyAO.getBody().get("resource_type").toString())){
            return PayResultBO.builder().success(false).build();
        }
        Map<String,Object> resource = (HashMap)payNotifyAO.getBody().get("resource");
        String resourceStr = new AesUtil(tencentConfig.getApiKey().getBytes()).decryptToString(resource.get("associated_data").toString().getBytes(), resource.get("nonce").toString().getBytes(),
                resource.get("ciphertext").toString());
        log.info("wx pay resource str:{}", resourceStr);
        HashMap resourceMap = JsonUtil.parseObject(resourceStr, HashMap.class);
        return preparePayResult(resourceMap);
    }

    @Override
    public CreateOrderResultBO createOrder(Order order) throws IOException {
        //请求URL
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/app");
        // 请求body参数
        String reqdata = JsonUtil.toJSONString(prepareAppOrder(order));
        StringEntity entity = new StringEntity(reqdata,"utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
        //完成签名并执行请求
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) { //处理成功
                String strResult = EntityUtils.toString(response.getEntity());
                log.info("order no:{} create order success,return body:{} ", order.getOrderNo(), strResult);
                Map<String, Object> result = JsonUtil.parseObject(strResult, HashMap.class);
                return CreateOrderResultBO.builder()
                        .authorizationTransactionCode(result.get("prepay_id").toString())
                        .orderNo(order.getOrderNo())
                        .build();
            } else {
                log.error("order no:{} failed,resp code:{},return body {}", EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        }
        finally {
            response.close();
        }
    }

    @Override
    public QueryOrderResultBO queryOrder(String orderNo) {
        try {
            //请求URL
            URIBuilder uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/pay/transactions/id/" + orderNo);
            uriBuilder.setParameter("mchid", tencentConfig.getMchid());

            //完成签名并执行请求
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.addHeader("Accept", "application/json");
            CloseableHttpResponse response = httpClient.execute(httpGet);

            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HashMap queryOrderResult = JsonUtil.parseObject(EntityUtils.toString(response.getEntity()), HashMap.class);
                    return prepareOrder(queryOrderResult);

                } else {
                    log.error("query order error, status code:{} body:{}", statusCode, EntityUtils.toString(response.getEntity()));
                    throw new IOException("request failed");
                }
            } finally {
                response.close();
            }
        }
        catch (Exception e){
            throw new ApiException("query order error", e);
        }
    }

    @Override
    public void closeOrder(String orderNo) throws IOException {

        //请求URL
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/" + orderNo + "/close");
        //请求body参数
        String reqdata ="{\"mchid\": \""+tencentConfig.getMchid()+"\"}";

        StringEntity entity = new StringEntity(reqdata,"utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                System.out.println("success,return body = " + EntityUtils.toString(response.getEntity()));
            } else if (statusCode == 204) {
                System.out.println("success");
            } else {
                System.out.println("failed,resp code = " + statusCode+ ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        } finally {
            response.close();
        }
    }

    @Override
    public PayMethodEnum getPayMethod() {
        return PayMethodEnum.Wx;
    }


    private Map<String, Object> prepareAppOrder(Order order){
        Map<String, Object> result = new HashMap<>();
        result.put("appid", tencentConfig.getAppId());
        result.put("mchid", tencentConfig.getMchid());
        result.put("description", order.getDescription());
        result.put("out_trade_no", order.getOrderNo());
        result.put("notify_url", tencentConfig.getNotifyUrl());
        Map<String, Object> amount = new HashMap<>();
        amount.put("total", order.getOrderTotal());
        amount.put("currency", "CNY");
        result.put("amount", amount);
        return result;
    }

    private PayResultBO preparePayResult(Map<String, Object> src){
        PayResultBO payNotifyResultBO = new PayResultBO();
        payNotifyResultBO.setOrderNo(src.get("out_trade_no").toString());
        payNotifyResultBO.setPayMethod(getPayMethod());
        payNotifyResultBO.setPaymentStatus(preparePaymentStatus(src.get("trade_state").toString()));
        payNotifyResultBO.setSuccess(true);
        return payNotifyResultBO;
    }

    private QueryOrderResultBO prepareOrder(Map<String, Object> src){
        QueryOrderResultBO queryOrderResultBO = new QueryOrderResultBO();
        queryOrderResultBO.setOrderNo(src.get("out_trade_no").toString());
        queryOrderResultBO.setPayMethodId(PayMethodEnum.Wx.getValue());
        queryOrderResultBO.setPaymentStatus(preparePaymentStatus(src.get("trade_state").toString()).getValue());
        return queryOrderResultBO;
    }

    private PaymentStatusEnum preparePaymentStatus(String src){
        switch (src){
            case "SUCCESS":
                return PaymentStatusEnum.SUCCESS;
            case "REFUND":
                return PaymentStatusEnum.REFUNDED;
            case "NOTPAY":
                return PaymentStatusEnum.NOTPAY;
            case "CLOSED":
                return PaymentStatusEnum.CLOSED;
            case "REVOKED":
                return PaymentStatusEnum.REVOKED;
            case "USERPAYING":
                return PaymentStatusEnum.USERPAYING;
            case "PAYERROR":
                return PaymentStatusEnum.REFUNDED;
            default:
                throw new IllegalStateException(src);
        }
    }

    public static class AesUtil {

        static final int KEY_LENGTH_BYTE = 32;
        static final int TAG_LENGTH_BIT = 128;
        private final byte[] aesKey;

        public AesUtil(byte[] key) {
            if (key.length != KEY_LENGTH_BYTE) {
                throw new IllegalArgumentException("无效的ApiV3Key，长度必须为32个字节");
            }
            this.aesKey = key;
        }

        public String decryptToString(byte[] associatedData, byte[] nonce, String ciphertext)
                throws GeneralSecurityException, IOException {
            try {
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

                SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
                GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);

                cipher.init(Cipher.DECRYPT_MODE, key, spec);
                cipher.updateAAD(associatedData);

                return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), "utf-8");
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new IllegalStateException(e);
            } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
