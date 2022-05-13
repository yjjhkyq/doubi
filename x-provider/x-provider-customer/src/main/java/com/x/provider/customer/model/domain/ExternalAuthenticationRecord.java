package com.x.provider.customer.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("external_authentication_record")
public class ExternalAuthenticationRecord extends BaseEntity {
  @TableId
  private long id;
  private Long customerId;
  private String externalIdentifier;
  private String unionExternalIdentifier;
  private String oauthAccessToken;
  private Integer provider;
  private String oauthToken;
}
