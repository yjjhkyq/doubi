package com.x.provider.pay.model.domain.order;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("order_item")
public class OrderItem extends BaseEntity {
  @TableId
  private Long id;
  private Long orderId;
  private Long customerId;
  private Integer productType;
  private Long productId;
  private Long quantity;
  private Long originalProductCost;
  private Long discountAmount;
  private Long payProductCost;
  private Date createdOnUtc;
  private Date updatedOnUtc;
}
