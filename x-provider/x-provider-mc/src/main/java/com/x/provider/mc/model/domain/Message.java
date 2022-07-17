package com.x.provider.mc.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

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
    private Long fromCustomerId;
    @Builder.Default
    private Long toCustomerId = 0L;
    @Builder.Default
    private Long toGroupId = 0L;
    private String messageType;
    private String alertMsg;
    private String msgBody;
    private Integer messageClass;
}
