package com.x.provider.oss.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.x.provider.api.oss.enums.GreenDataTypeEnum;
import com.x.core.domain.SuggestionTypeEnum;
import com.x.provider.api.oss.model.ao.AttributeGreenRpcAO;
import com.x.provider.api.oss.model.ao.GreenRpcAO;
import com.x.provider.api.oss.model.dto.AttributeGreenResultDTO;
import com.x.provider.oss.configure.AliConfig;
import com.x.provider.oss.service.GreenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author: liushenyi
 * @date: 2021/11/11/15:12
 */
@Slf4j
@Service("aliGreenService")
public class AliGreenService implements GreenService {

    private final AliConfig aliConfig;

    public AliGreenService(AliConfig aliConfig){
        this.aliConfig = aliConfig;
    }

    @Override
    public void greenAttributeAsync(AttributeGreenRpcAO attributeGreenRpcAO) {

    }

    @Override
    public AttributeGreenResultDTO greenAttributeSync(AttributeGreenRpcAO attributeGreenRpcAO) {
        return null;
    }

    @Override
    public void onGreenResultNotify(Map<String, Object> result) {

    }

    @Override
    public SuggestionTypeEnum green(GreenRpcAO greenRpcAO) {
        if (greenRpcAO.getDataType().equals(GreenDataTypeEnum.TEXT.name())){
            return greenText(greenRpcAO.getValue());
        }
        throw new IllegalStateException("not support from data type:" + greenRpcAO.getDataType());
    }

    private SuggestionTypeEnum greenText(String content){
        IClientProfile profile = DefaultProfile
                .getProfile("cn-shanghai", aliConfig.getAccessKeyId(), aliConfig.getAccessKeySecret());
        DefaultProfile
                .addEndpoint("cn-shanghai", "Green", "green.cn-shanghai.aliyuncs.com");
        IAcsClient client = new DefaultAcsClient(profile);
        TextScanRequest textScanRequest = new TextScanRequest();
        textScanRequest.setAcceptFormat(FormatType.JSON); // ??????API???????????????
        textScanRequest.setHttpContentType(FormatType.JSON);
        textScanRequest.setMethod(com.aliyuncs.http.MethodType.POST); // ?????????????????????
        textScanRequest.setEncoding("UTF-8");
        textScanRequest.setRegionId("cn-shanghai");
        List<Map<String, Object>> tasks = new ArrayList<>();
        Map<String, Object> task1 = new LinkedHashMap<>();
        task1.put("dataId", UUID.randomUUID().toString());
        /**
         * ????????????????????????????????????10000????????????
         */
        task1.put("content", content);
        tasks.add(task1);
        JSONObject data = new JSONObject();

        /**
         * ??????????????????????????????????????????antispam???
         **/
        data.put("scenes", Arrays.asList("antispam"));
        data.put("tasks", tasks);
        try {
            textScanRequest.setHttpContent(data.toJSONString().getBytes("UTF-8"), "UTF-8", FormatType.JSON);
            // ??????????????????????????????
            textScanRequest.setConnectTimeout(3000);
            textScanRequest.setReadTimeout(6000);
            HttpResponse httpResponse = client.doAction(textScanRequest);
            if (httpResponse.isSuccess()) {
                JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
                System.out.println(JSON.toJSONString(scrResponse, true));
                if (200 == scrResponse.getInteger("code")) {
                    JSONArray taskResults = scrResponse.getJSONArray("data");
                    for (Object taskResult : taskResults) {
                        if (200 == ((JSONObject) taskResult).getInteger("code")) {
                            JSONArray sceneResults = ((JSONObject) taskResult).getJSONArray("results");
                            for (Object sceneResult : sceneResults) {
                                String scene = ((JSONObject) sceneResult).getString("scene");
                                String suggestion = ((JSONObject) sceneResult).getString("suggestion");
                                // ??????scene???suggetion??????????????????
                                // suggestion???pass????????????????????????suggestion???block????????????????????????????????????label????????????????????????????????????
                                log.info("args = [" + scene + "]");
                                log.info("args = [" + suggestion + "]");
                                return prepare(suggestion);
                            }
                        } else {
                            log.error("task process fail:" + ((JSONObject) taskResult).getInteger("code"));
                            throw new RuntimeException("task process fail:" + ((JSONObject) taskResult).getInteger("code"));
                        }
                    }
                    throw new RuntimeException("green result is empty, content:" + content);
                } else {
                    log.error("detect not success. code:" + scrResponse.getInteger("code"));
                    throw new RuntimeException("detect not success. code:" + scrResponse.getInteger("code"));
                }
            } else {
                log.error("response not success. status:" + httpResponse.getStatus());
                throw new RuntimeException("response not success. status:" + httpResponse.getStatus());
            }
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private SuggestionTypeEnum prepare(String suggestion){
        for (SuggestionTypeEnum item:
                SuggestionTypeEnum.values()) {
            if (item.name().toLowerCase().equals(suggestion)){
                return item;
            }
        }
        throw new IllegalStateException("convert to suggestion type error, suggestion:" + suggestion);
    }
}
