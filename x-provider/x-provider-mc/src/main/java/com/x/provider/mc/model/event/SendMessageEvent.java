package com.x.provider.mc.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: liushenyi
 * @date: 2022/07/22/17:04
 */
public class SendMessageEvent {
    private Long toCustomerId;
    private Long toGroupId;
    private String message;

    public SendMessageEvent(){

    }

    public SendMessageEvent(Long toCustomerId, Long toGroupId, String message){
        this.toCustomerId = toCustomerId;
        this.toGroupId = toGroupId;
        this.message = message;
    }

    public Long getToCustomerId() {
        return toCustomerId;
    }

    public void setToCustomerId(Long toCustomerId) {
        this.toCustomerId = toCustomerId;
    }

    public Long getToGroupId() {
        return toGroupId;
    }

    public void setToGroupId(Long toGroupId) {
        this.toGroupId = toGroupId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
