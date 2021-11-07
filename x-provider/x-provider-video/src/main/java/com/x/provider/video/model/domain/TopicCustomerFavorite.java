package com.x.provider.video.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import io.swagger.models.auth.In;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("topic_customer_favorite")
public class TopicCustomerFavorite extends BaseEntity {
  @TableId
  private Long id;
  private Long customerId;
  private Long topicId;
  private Boolean favorite;
  private Integer topicSourceType;

}
