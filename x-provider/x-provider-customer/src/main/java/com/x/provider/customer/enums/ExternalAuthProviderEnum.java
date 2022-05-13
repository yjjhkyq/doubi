package com.x.provider.customer.enums;

import com.x.core.enums.IntegerEnum;

public enum ExternalAuthProviderEnum implements IntegerEnum {
    /**
     * 微信小程序登陆
     */
    WX_MICRO_APP(1);

    private final Integer value;

    ExternalAuthProviderEnum(Integer value){
        this.value = value;
    }


    @Override
    public Integer getValue() {
        return this.value;
    }
}
