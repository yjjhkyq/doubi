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
  private long id;

  @TableField("from_customer_id")
  private long fromCustomerId;

  @TableField("to_customer_id")
  private long toCustomerId;

  @TableField("relation")
  private int relation;
}
