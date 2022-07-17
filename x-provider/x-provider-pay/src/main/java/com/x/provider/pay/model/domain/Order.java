package com.x.provider.pay.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("`order`")
public class Order extends BaseEntity {
  @TableId
  private Long id;
  private Long customerId;
  private Integer orderStatus;
  private Integer paymentStatus;
  private Integer currencyCode;
  private Integer payMethodId;
  private Long orderTotal;
  private Long refundedAmount;
  private Date refundedDate;
  private Date paidDate;
  private Integer productType;
}
