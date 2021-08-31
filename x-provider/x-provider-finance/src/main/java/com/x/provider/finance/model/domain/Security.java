package com.x.provider.finance.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@TableName("security")
public class Security extends BaseEntity {
  @TableId
  private long id;
  private String code;
  private String symbol;
  private String name;
  private String fullName;
  private String enName;
  private String cnSpell;
  private String exchange;
  private String type;
  private String parentCode;
}
