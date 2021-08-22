package com.x.provider.customer.model.domain;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("generic_attribute")
public class GenericAttribute  extends BaseEntity {

  @TableId
  private long id;
  private long entityId;
  private String keyGroup;
  private String key;
  private String value;
}
