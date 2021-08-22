package com.x.provider.customer.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("customer_password")
public class CustomerPassword extends BaseEntity {
 @TableId
  private long id;
  private long customerId;
  private String password;
  private String passwordSalt;

  public CustomerPassword(long customerId, String passwordSalt){
      this.customerId = customerId;
      this.passwordSalt = passwordSalt;
  }
}
