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
@TableName("message_sender_system")
public class MessageSenderSystem extends BaseEntity {
    @TableId
    private Long id;
    private Long customerId;
    private Integer targetType;
    private Long targetId;
    private Integer msgExpireDays;
    private Boolean save;
    private Boolean im;
}
