package com.x.provider.customer.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@TableName("customer_relation")
public class CustomerRelation extends BaseEntity {

  @TableId
  private Long id;

  @TableField("from_customer_id")
  private Long fromCustomerId;

  @TableField("to_customer_id")
  private Long toCustomerId;

  @TableField("is_follow")
  private Boolean follow;

  @TableField("is_friend")
  private Boolean friend;

}
