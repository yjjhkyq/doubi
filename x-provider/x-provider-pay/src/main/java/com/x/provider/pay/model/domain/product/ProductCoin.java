package com.x.provider.pay.model.domain.product;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("product_coin")
public class ProductCoin {
    @TableId
    private Long id;
    private Long productId;
    private Long coin;
}
