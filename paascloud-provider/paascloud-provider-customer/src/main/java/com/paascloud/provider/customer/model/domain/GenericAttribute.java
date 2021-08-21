package com.paascloud.provider.customer.model.domain;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.paascloud.core.domain.BaseEntity;
import lombok.Data;

@Data
@TableName("generic_attribute")
public class GenericAttribute  extends BaseEntity {

  @TableId
  private long id;
  private long entityId;
  private String keyGroup;
  private String key;
  private String value;
}
