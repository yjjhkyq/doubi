package com.x.provider.api.pay.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO {
  private Long customerId;
  private Integer orderStatus;
  private Integer paymentStatus;
  private Integer currencyCode;
  private Integer payMethodId;
  private Long orderTotal;
  private Integer productType;
  private List<CreateOrderItemDTO> orderItemList;
}
