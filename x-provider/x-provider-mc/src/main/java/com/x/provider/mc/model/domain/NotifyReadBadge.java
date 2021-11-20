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
@TableName("notify_read_badge")
public class NotifyReadBadge extends BaseEntity {
    @TableId
    private Long id;
    private Long targetUid;
    private Long senderUid;
    private String shortMsg;
    private boolean hasUnreadMsg;
    private Long readEndNotifyId;
}
