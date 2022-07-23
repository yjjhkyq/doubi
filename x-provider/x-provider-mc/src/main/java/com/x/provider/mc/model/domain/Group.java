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
@TableName("message_group")
public class Group extends BaseEntity {
    @TableId
    private Long id;
    private Long customerId;
    private String groupName;
    private Integer groupType;
    private Long displayOrder;
    private Integer messageLivedMilliSeconds;
}
