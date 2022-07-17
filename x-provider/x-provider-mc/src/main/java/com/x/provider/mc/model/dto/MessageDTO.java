package com.x.provider.mc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private Long fromCustomerId;
    private Long toCustomerId = 0L;
    private Long toGroupId = 0L;
    private String messageType;
    private Integer messageClass;
    private String alertMsg;
    private String msgBody;

    private String fromCustomerAvatarUrl;
    private String fromCustomerNickName;
}
