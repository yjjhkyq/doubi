package com.x.provider.mc.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: liushenyi
 * @date: 2022/08/12/10:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetConversationRequestBO {
    private String conversationId;
    private Long customerId;
    private Long groupId;
    private Long ownerCustomerId;
}
