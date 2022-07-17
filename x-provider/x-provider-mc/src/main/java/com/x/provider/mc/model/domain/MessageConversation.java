package com.x.provider.mc.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("message_conversation")
public class MessageConversation extends BaseEntity {
    @TableId
    private Long id;
    private Long messageId;
    private Long groupId;
    private Long customerId;
    @TableField("is_read")
    private Boolean read;
}
