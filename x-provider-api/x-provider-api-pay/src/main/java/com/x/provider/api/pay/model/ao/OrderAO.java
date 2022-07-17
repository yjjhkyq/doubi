package com.x.provider.api.pay.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAO {
  private Long customerId;
  private Integer orderStatus;
  private Integer paymentStatus;
  private Integer currencyCode;
  private Integer payMethodId;
  private Long orderTotal;
  private Integer productType;
  private List<OrderItemAO> orderItemList;
}
