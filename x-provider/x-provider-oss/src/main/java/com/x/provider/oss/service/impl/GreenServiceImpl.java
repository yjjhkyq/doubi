package com.x.provider.oss.service.impl;

import com.x.provider.api.oss.enums.GreenDataTypeEnum;
import com.x.provider.api.oss.enums.SuggestionTypeEnum;
import com.x.provider.api.oss.model.ao.AttributeGreenRpcAO;
import com.x.provider.api.oss.model.ao.GreenRpcAO;
import com.x.provider.api.oss.model.dto.AttributeGreenResultDTO;
import com.x.provider.oss.service.GreenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service("greenService")
public class GreenServiceImpl implements GreenService {

    private final RestTemplate restTemplate;
    private final GreenService baiduGreenService;
    private final GreenService tencentGreenService;

    public GreenServiceImpl(RestTemplate restTemplate,
                            @Qualifier("baiduGreenService") GreenService baiduGreenService,
                            @Qualifier("tencentGreenService") GreenService tencentGreenService){
        this.restTemplate = restTemplate;
        this.baiduGreenService = baiduGreenService;
        this.tencentGreenService = tencentGreenService;
    }

    @Override
    public void greenAttributeAsync(AttributeGreenRpcAO attributeGreen) {
        getGreenService(GreenDataTypeEnum.valueOf(attributeGreen.getDataType())).greenAttributeAsync(attributeGreen);
    }

    @Override
    public AttributeGreenResultDTO greenAttributeSync(AttributeGreenRpcAO attributeGreenRpcAO) {
        return getGreenService(GreenDataTypeEnum.valueOf(attributeGreenRpcAO.getDataType())).greenAttributeSync(attributeGreenRpcAO);
    }

    @Override
    public void onGreenResultNotify(Map<String, Object> result) {
        baiduGreenService.onGreenResultNotify(result);
        tencentGreenService.onGreenResultNotify(result);
    }

    @Override
    public SuggestionTypeEnum green(GreenRpcAO greenRpcAO) {
        return null;
    }

    private GreenService getGreenService(GreenDataTypeEnum greenDataType){
        if (GreenDataTypeEnum.TEXT.equals(greenDataType)){
            return baiduGreenService;
        }
        return tencentGreenService;
    }
}
