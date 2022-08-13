package com.x.provider.mc.model.query;

import com.x.core.web.page.PageLimit;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageConversationQuery {
    private Long id;
    private Long ownerCustomerId;
    private String conversationId;
    private Long ltId;
    private PageLimit pageLimit;
}
