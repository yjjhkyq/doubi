package com.x.provider.video.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("customer_topic")
public class CustomerTopic extends BaseEntity {
  @TableId
  private Long id;
  private Long customerId;
  private Long topicId;
  private Long favorite;

}
