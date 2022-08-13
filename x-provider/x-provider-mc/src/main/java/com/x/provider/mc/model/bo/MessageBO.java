package com.x.provider.mc.model.bo;

import com.x.provider.api.mc.model.protocol.CommonMessageBodyDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageBO {
    private Long id;
    private Long fromCustomerId;
    private Long toCustomerId = 0L;
    private Long toGroupId = 0L;
    private String messageType;
    private Integer messageClass;
    private String alertMsg;
    private String msgBody;
    private Date createdOnUtc;
    private Long createdTimestamp;

    private String fromCustomerAvatarUrl;
    private String fromCustomerNickName;

    private CommonMessageBodyDTO textBody;
    private CommonMessageBodyDTO imageBody;
    private CommonMessageBodyDTO videoBody;
    private CommonMessageBodyDTO voiceBody;

}
