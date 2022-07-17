package com.x.provider.mc.model.query;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

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
