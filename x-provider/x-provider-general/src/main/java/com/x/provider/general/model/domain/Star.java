package com.x.provider.general.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("star")
public class Star extends BaseEntity {
  @TableId
  private Long id;
  private Integer itemType;
  private Long itemId;
  private Long starCustomerId;
  @TableField(value = "is_star")
  private boolean star;
}
