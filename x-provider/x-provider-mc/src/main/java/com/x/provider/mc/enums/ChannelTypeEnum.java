package com.x.provider.mc.enums;

public enum  ChannelTypeEnum {
    PERSONAL_RECEIVE_MESSAGE(0L),
    PERSONAL_STATE(0L),
    ;

    private Long senderUserId;

    ChannelTypeEnum(Long senderUserId){
        this.senderUserId = senderUserId;
    }


}
