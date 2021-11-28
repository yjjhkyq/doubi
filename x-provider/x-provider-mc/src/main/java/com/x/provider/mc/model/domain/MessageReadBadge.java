package com.x.provider.mc.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("message_read_badge")
public class MessageReadBadge extends BaseEntity {
    @TableId
    private Long id;
    private Long customerId;
    private Long senderUid;
    private String alertMsg;
    private Boolean hasUnreadMsg;
    private Long readEndNotifyId;
}
