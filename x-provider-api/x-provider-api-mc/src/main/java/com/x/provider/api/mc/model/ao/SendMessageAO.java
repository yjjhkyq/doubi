package com.x.provider.api.mc.model.ao;

import lombok.Data;

@Data
public class SendMessageAO {
    private Long fromCustomerId;
    private Long toCustomerId = 0L;
    private Long toGroupId = 0L;
    private String alertMsg;
    private String msgBody;
    private String messageType;
    private Boolean onlineUserOnly;
    private Integer messageClass;

    public SendMessageAO(){

    }

    public SendMessageAO(Long fromCustomerId, Long toGroupId, Long toCustomerId, String messageType, String alertMsg, String msgBody, Integer messageClass, Boolean onlineUserOnly){
        this.fromCustomerId = fromCustomerId;
        this.toCustomerId = toCustomerId;
        this.toGroupId = toGroupId;
        this.alertMsg = alertMsg;
        this.msgBody = msgBody;
        this.messageType = messageType;
        this.messageClass = messageClass;
        this.onlineUserOnly = onlineUserOnly;
    }
}
