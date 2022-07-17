package com.x.provider.api.mc.model.protocol;

import com.x.core.enums.IntegerEnum;

/**
 * 消息类型
 */
public enum MessageClassEnum implements IntegerEnum {
    IM(1),
    SYSTEM_NOTIFY(2),
    ;

    private Integer value;

   MessageClassEnum(Integer value){
       this.value = value;
   }

    @Override
    public Integer getValue() {
        return value;
    }
}
