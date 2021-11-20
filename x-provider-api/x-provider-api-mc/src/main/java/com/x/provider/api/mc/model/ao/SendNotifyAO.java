package com.x.provider.api.mc.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotifyAO {
    private Long senderUid;
    private Long targetId;
    private String shortMsg;
    private String msgBody;
}
