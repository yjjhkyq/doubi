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
@TableName("notify_sender")
public class NotifySender extends BaseEntity {
    @TableId
    private Long id;
    private Long senderUid;
    private Integer senderType;
    private Integer targetType;
    private Integer msgExpireDays;
    private Boolean save;
}
