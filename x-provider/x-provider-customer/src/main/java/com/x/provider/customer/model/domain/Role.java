package com.x.provider.customer.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.Data;

@Data
@TableName("role")
public class Role extends BaseEntity {
  @TableId
  private long id;
  private String name;
  @TableField("is_system_role")
  private boolean systemRole;
  private String systemName;
}
