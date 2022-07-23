package com.x.provider.oss.service.impl;

import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.contentcensor.EImgType;
import com.x.provider.api.oss.enums.GreenDataTypeEnum;
import com.x.core.domain.SuggestionTypeEnum;
import com.x.provider.api.oss.model.ao.GreenRpcAO;
import com.x.provider.api.oss.model.dto.AttributeGreenResultDTO;
import com.x.provider.api.oss.model.ao.AttributeGreenRpcAO;
import com.x.provider.oss.configure.BaiduConfig;
import com.x.provider.oss.service.GreenService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service("baiduGreenService")
public class BaiduGreenServiceImpl implements GreenService {

    private final BaiduConfig baiduConfig;

    public BaiduGreenServiceImpl(BaiduConfig baiduConfig){
        this.baiduConfig = baiduConfig;
    }

    @Override
    public void greenAttributeAsync(AttributeGreenRpcAO attributeGreenRpcAO) {
        throw new IllegalStateException("not supported");
    }

    @Override
    public AttributeGreenResultDTO greenAttributeSync(AttributeGreenRpcAO attributeGreenRpcAO) {
        AttributeGreenResultDTO greenResultRpcAO = new AttributeGreenResultDTO();
        BeanUtils.copyProperties(attributeGreenRpcAO, greenResultRpcAO);
        GreenDataTypeEnum greenDataType = GreenDataTypeEnum.valueOf(attributeGreenRpcAO.getDataType());
        AipContentCensor client = new AipContentCensor(baiduConfig.getAppId(), baiduConfig.getApiKey(), baiduConfig.getSecretKey());
        JSONObject parseResult = null;
        switch (greenDataType){
            case PICTURE:
            case VIDEO:
                parseResult = client.imageCensorUserDefined(attributeGreenRpcAO.getValue(), EImgType.URL, null);
                break;
            case TEXT:
                parseResult = client.textCensorUserDefined(attributeGreenRpcAO.getValue());
                break;
        }
        greenResultRpcAO.setSuggestionType(parseGreenResult(parseResult).toString());
        return greenResultRpcAO;
    }

    @Override
    public void onGreenResultNotify(Map<String, Object> result) {
    }

    @Override
    public SuggestionTypeEnum green(GreenRpcAO greenRpcAO) {
        AipContentCensor client = new AipContentCensor(baiduConfig.getAppId(), baiduConfig.getApiKey(), baiduConfig.getSecretKey());
        JSONObject parseResult = null;
        GreenDataTypeEnum greenDataTypeEnum = GreenDataTypeEnum.valueOf(greenRpcAO.getDataType());
        switch (greenDataTypeEnum){
            case PICTURE:
            case VIDEO:
                parseResult = client.imageCensorUserDefined(greenRpcAO.getValue(), EImgType.URL, null);
                break;
            case TEXT:
                parseResult = client.textCensorUserDefined(greenRpcAO.getValue());
                break;
        }
        return parseGreenResult(parseResult);
    }

    private SuggestionTypeEnum parseGreenResult(JSONObject jsonObject){
        SuggestionTypeEnum result = SuggestionTypeEnum.PASS;
        if (jsonObject.has("error_code")){
            log.error("baidu green sdk error, error code: {} eror msg: {}", jsonObject.getLong("error_code"), jsonObject.getString("error_msg"));
            return result;
        }
        if (jsonObject.has("+error_code")){
            log.error("baidu green sdk error, error code: {} eror msg: {}", jsonObject.getLong("+error_code"), jsonObject.getString("+error_msg"));
            return result;
        }
        final int conclusionType = jsonObject.getInt("conclusionType");
        switch (conclusionType){
            case 1:
                result = SuggestionTypeEnum.PASS;
                break;
            case 2:
                result = SuggestionTypeEnum.BLOCK;
                break;
            case 3:
                result = SuggestionTypeEnum.REVIEW;
            case 4:
                //百度审核失败
                result = SuggestionTypeEnum.BLOCK;
                break;
        }
        return result;
    }
}
