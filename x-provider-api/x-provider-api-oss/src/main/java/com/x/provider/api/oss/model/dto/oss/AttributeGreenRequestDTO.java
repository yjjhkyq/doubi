package com.x.provider.api.oss.model.dto.oss;

import com.x.provider.api.oss.enums.GreenDataTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttributeGreenRequestDTO {
    private String entityId;
    private String keyGroup;
    private String key;
    private String value;
    private String dataType;
    private String callbackUrl;

    public AttributeGreenRequestDTO(){

    }

    public AttributeGreenRequestDTO(String keyGroup, String key, String entityId, String value, GreenDataTypeEnum greenDataType){
        this.keyGroup = keyGroup;
        this.key = key;
        this.entityId = entityId;
        this.value = value;
        this.dataType = greenDataType.toString();
    }

    public AttributeGreenRequestDTO(String keyGroup, String key, String entityId, String value, GreenDataTypeEnum greenDataType, String callbackUrl){
        this.keyGroup = keyGroup;
        this.key = key;
        this.entityId = entityId;
        this.value = value;
        this.dataType = greenDataType.toString();
        this.callbackUrl = callbackUrl;
    }

    public AttributeGreenRequestDTO(String keyGroup, long entityId, String key, String value, GreenDataTypeEnum greenDataType){
        this.keyGroup = keyGroup;
        this.key = key;
        this.entityId = String.valueOf(entityId);
        this.value = value;
        this.dataType = greenDataType.toString();
    }

    public AttributeGreenRequestDTO(String keyGroup, long entityId, String key, String value, GreenDataTypeEnum greenDataType, String callbackUrl){
        this.keyGroup = keyGroup;
        this.key = key;
        this.entityId = String.valueOf(entityId);
        this.value = value;
        this.dataType = greenDataType.toString();
        this.callbackUrl = callbackUrl;
    }
}
