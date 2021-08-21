package com.paascloud.provider.customer.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.paascloud.core.domain.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("customer_role")
public class CustomerRole extends BaseEntity {
  @TableId
  private long id;
  private long customerId;
  private long roleId;

  public CustomerRole(long customerId, long roleId){
    this.customerId = customerId;
    this.roleId = roleId;
  }
}
