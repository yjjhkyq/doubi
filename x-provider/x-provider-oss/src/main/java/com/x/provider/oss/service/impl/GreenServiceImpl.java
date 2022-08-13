package com.x.provider.oss.service.impl;

import com.x.provider.api.oss.enums.GreenDataTypeEnum;
import com.x.core.domain.SuggestionTypeEnum;
import com.x.provider.api.oss.model.dto.oss.AttributeGreenRequestDTO;
import com.x.provider.api.oss.model.dto.oss.GreenRequestDTO;
import com.x.provider.api.oss.model.dto.oss.AttributeGreenResultDTO;
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
    private final GreenService aliGreenService;
    private final GreenService tencentGreenService;

    public GreenServiceImpl(RestTemplate restTemplate,
                            @Qualifier("aliGreenService") GreenService aliGreenService,
                            @Qualifier("tencentGreenService") GreenService tencentGreenService){
        this.restTemplate = restTemplate;
        this.aliGreenService = aliGreenService;
        this.tencentGreenService = tencentGreenService;
    }

    @Override
    public void greenAttributeAsync(AttributeGreenRequestDTO attributeGreen) {
        getGreenService(GreenDataTypeEnum.valueOf(attributeGreen.getDataType())).greenAttributeAsync(attributeGreen);
    }

    @Override
    public AttributeGreenResultDTO greenAttributeSync(AttributeGreenRequestDTO attributeGreenRpcAO) {
        return getGreenService(GreenDataTypeEnum.valueOf(attributeGreenRpcAO.getDataType())).greenAttributeSync(attributeGreenRpcAO);
    }

    @Override
    public void onGreenResultNotify(Map<String, Object> result) {
        aliGreenService.onGreenResultNotify(result);
        tencentGreenService.onGreenResultNotify(result);
    }

    @Override
    public SuggestionTypeEnum green(GreenRequestDTO greenRpcAO) {
        return getGreenService(GreenDataTypeEnum.valueOf(greenRpcAO.getDataType())).green(greenRpcAO);
    }

    private GreenService getGreenService(GreenDataTypeEnum greenDataType){
        if (GreenDataTypeEnum.TEXT.equals(greenDataType)){
            return aliGreenService;
        }
        return tencentGreenService;
    }
}
