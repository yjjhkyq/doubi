package com.x.provider.mc.model.query;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationQuery {
    private Long id;
    private Long customerId;
    private Long groupId;
    private Long ownerCustomerId;
    private Long ltDisplayOrder;
    private String conversationId;
}
