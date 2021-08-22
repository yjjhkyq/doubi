package com.x.provider.api.oss.model.dto;

import lombok.Data;

@Data
public class AttributeGreenResultDTO {
    private String entityId;
    private String keyGroup;
    private String key;
    private String value;
    private String suggestionType;
}
