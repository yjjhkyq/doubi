package com.x.provider.oss.service;


import com.x.provider.api.oss.model.ao.AttributeGreenRpcAO;
import com.x.provider.api.oss.model.dto.AttributeGreenResultDTO;

import java.util.Map;

public interface GreenService {
    void greenAttributeAsync(AttributeGreenRpcAO attributeGreenRpcAO);
    AttributeGreenResultDTO greenAttributeSync(AttributeGreenRpcAO attributeGreenRpcAO);
    void onGreenResultNotify(Map<String, Object> result);
}
