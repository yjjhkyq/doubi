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
@TableName("message")
public class Message extends BaseEntity {
    @TableId
    private Long id;
    private Long senderUid;
    private Long targetId;
    private String messageType;
    private String alertMsg;
    private String msgBody;
    private Date expireDate;
}
