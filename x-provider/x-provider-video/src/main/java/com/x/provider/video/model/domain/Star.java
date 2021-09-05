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
@TableName("star")
public class Star extends BaseEntity {
  @TableId
  private long id;
  private String itemId;
  private long starCustomerId;
  private long isStar;
  private long itemType;

}
