package com.x.provider.mc.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdOnUtc;
    private Long createdTimestamp;

    private String fromCustomerAvatarUrl;
    private String fromCustomerNickName;
}
