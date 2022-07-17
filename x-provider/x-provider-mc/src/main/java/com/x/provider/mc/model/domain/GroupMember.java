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
@TableName("group_member")
public class GroupMember extends BaseEntity {
    @TableId
    private Long id;
    private Long groupId;
    private Long memberId;
}
