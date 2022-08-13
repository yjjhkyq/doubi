package com.x.provider.api.pay.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemDTO {
  private Long customerId;
  private Integer productType;
  private Long productId;
  private Long quantity;
  private Long originalProductCost;
  private Long discountAmount;
  private Long payProductCost;
}
