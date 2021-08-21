package com.paascloud.provider.customer.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.paascloud.core.domain.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("customer")
public class Customer extends BaseEntity {
  @TableId
  private long id;
  private String userName;
  private String email;
  @TableField("is_active")
  private boolean active = true;
  @TableField("is_system_account")
  private boolean systemAccount = false;
  private String phone;
  public Customer(String userName){
    this.userName = userName;
  }
}
