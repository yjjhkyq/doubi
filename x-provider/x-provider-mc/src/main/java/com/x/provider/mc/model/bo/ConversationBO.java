package com.x.provider.mc.model.bo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationBO {
    private Long customerId;
    private Long groupId;
    private Long ownerCustomerId;
    private String alertMessage;
    private Integer conversationType;
    private Long unreadCount;
    private Long displayOrder;
    private String conversationId;
    private String showName;
    private String faceUrl;
}
