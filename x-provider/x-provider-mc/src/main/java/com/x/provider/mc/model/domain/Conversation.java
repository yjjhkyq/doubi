package com.x.provider.mc.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("conversation")
public class Conversation extends BaseEntity {
    @TableId
    private Long id;
    private Long customerId;
    private Long groupId;
    private Long ownerCustomerId;
    private String alertMessage;
    private Long lastReadMessageId;
    private Integer conversationType;
    private Long unreadCount;
    private Long displayOrder;
    private String conversationId;
}
