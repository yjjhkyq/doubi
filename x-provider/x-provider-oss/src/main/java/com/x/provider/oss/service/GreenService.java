package com.x.provider.oss.service;


import com.x.core.domain.SuggestionTypeEnum;
import com.x.provider.api.oss.model.dto.oss.AttributeGreenRequestDTO;
import com.x.provider.api.oss.model.dto.oss.GreenRequestDTO;
import com.x.provider.api.oss.model.dto.oss.AttributeGreenResultDTO;

import java.util.Map;

public interface GreenService {
    void greenAttributeAsync(AttributeGreenRequestDTO attributeGreenRpcAO);
    AttributeGreenResultDTO greenAttributeSync(AttributeGreenRequestDTO attributeGreenRpcAO);
    void onGreenResultNotify(Map<String, Object> result);
    SuggestionTypeEnum green(GreenRequestDTO greenRpcAO);
}
