package com.x.provider.finance.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("industry")
public class Industry extends BaseEntity {
  @TableId
  private long id;
  private String code;
  private String name;
  private String cnSpell;
  private Date startDate;
}
