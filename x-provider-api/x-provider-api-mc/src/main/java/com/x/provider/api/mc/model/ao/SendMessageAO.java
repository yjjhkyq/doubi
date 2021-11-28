package com.x.provider.api.mc.model.ao;

import lombok.Data;

@Data
public class SendMessageAO {
    private Long senderUid;
    private Long targetId;
    private Integer messageTargetType;
    private String alertMsg;
    private String msgBody;
    private String messageType;

    public SendMessageAO(){

    }

    public SendMessageAO(Long senderUid, Integer messageTargetType, Long targetId, String messageType, String alertMsg, String msgBody){
        this.senderUid = senderUid;
        this.targetId = targetId;
        this.alertMsg = alertMsg;
        this.msgBody = msgBody;
        this.messageType = messageType;
        this.messageTargetType = messageTargetType;
    }
}
