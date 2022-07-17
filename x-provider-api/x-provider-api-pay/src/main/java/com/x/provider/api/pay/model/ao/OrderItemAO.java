package com.x.provider.api.pay.model.ao;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemAO {
  private Long customerId;
  private Long productType;
  private Long productId;
  private Long quantity;
  private Long originalProductCost;
  private Long discountAmount;
  private Long payProductCost;
}
