package com.x.provider.customer.service;

import com.x.provider.customer.model.domain.GenericAttribute;

import java.util.List;
import java.util.Map;

public interface GenericAttributeService {
    List<GenericAttribute> listAttribute(String keyGroup, long entityId);
    Map<String, String> listAttributeMap(String keyGroup, long entityId);
    GenericAttribute getAttribute(String keyGroup, long entityId, String key);
    String getAttributeValue(String keyGroup, long entityId, String key);
    void addOrUpdateAttribute(String keyGroup, long entityId, String key, String value);
    void addOrUpdateDraftAttribute(String keyGroup, long entityId, String key, String value);
    void deleteDraftAttribute(String keyGroup, long entityId, String key);
    Map<Long, Map<String, String>> listAttributeMap(String keyGroup, List<Long> entityIdList);
}
