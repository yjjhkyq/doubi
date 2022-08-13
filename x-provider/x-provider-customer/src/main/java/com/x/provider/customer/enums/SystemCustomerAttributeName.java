package com.x.provider.customer.enums;

import com.x.provider.api.oss.enums.GreenDataTypeEnum;

public enum  SystemCustomerAttributeName {
    NICK_NAME(GreenDataTypeEnum.TEXT),
    AVATAR_ID(GreenDataTypeEnum.PICTURE),
    PERSONAL_HOMEPAGE_BACKGROUND_ID(GreenDataTypeEnum.PICTURE),
    SIGNATURE(GreenDataTypeEnum.TEXT),
    GENDER(GreenDataTypeEnum.NULL);

    private GreenDataTypeEnum greenDataType;

    SystemCustomerAttributeName(GreenDataTypeEnum greenDataType){
        this.greenDataType = greenDataType;
    }

    public GreenDataTypeEnum getGreenDataType() {
        return greenDataType;
    }
}
