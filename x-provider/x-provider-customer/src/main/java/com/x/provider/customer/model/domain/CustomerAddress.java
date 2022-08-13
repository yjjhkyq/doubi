package com.x.provider.customer.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("customer_address")
public class CustomerAddress extends BaseEntity {
  @TableId
  private Long id;
  private Long customerId;
  private Integer customerAddressType;
  private Integer countryId;
  private String countryName;
  private Integer provinceId;
  private String provinceName;
  private Integer cityId;
  private String cityName;
}
