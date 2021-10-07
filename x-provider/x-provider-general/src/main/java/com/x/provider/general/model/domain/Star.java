package com.x.provider.general.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("star")
public class Star extends BaseEntity {
  @TableId
  private long id;
  private long associationItemId;
  private long itemId;
  private long starCustomerId;
  private boolean isStar;
  private int itemType;
}
