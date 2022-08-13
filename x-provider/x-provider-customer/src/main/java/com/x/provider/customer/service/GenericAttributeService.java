package com.x.provider.customer.service;

import com.x.provider.customer.model.ao.AddOrUpdateAttributeAO;
import com.x.provider.customer.model.domain.GenericAttribute;

import java.util.List;
import java.util.Map;

public interface GenericAttributeService {
    List<GenericAttribute> listAttributeMap(String keyGroup, Long entityId);
    GenericAttribute getAttribute(String keyGroup, Long entityId, String key);
    GenericAttribute addOrUpdateAttribute(AddOrUpdateAttributeAO addOrUpdateAttributeAO);
    GenericAttribute addOrUpdateDraftAttribute(String keyGroup, Long entityId, String key, String value);
    void deleteDraftAttribute(String keyGroup, Long entityId, String key);
    List<GenericAttribute> listAttributeMap(String keyGroup, List<Long> entityIdList);
}
