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
  private long id;
  private long customerId;
  private long topicId;
  private long favorite;

}