package com.x.provider.pay.model.bo.payment;

import com.x.provider.api.pay.enums.PayMethodEnum;
import com.x.provider.pay.model.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderSimpleBO {
    private Integer payMethod;
    private Long productId;
}
