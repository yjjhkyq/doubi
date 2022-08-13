package com.x.provider.pay.model.domain.product;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("product_vip")
public class ProductVip {
    @TableId
    private Long id;
    private Long productId;
    private Integer level;
    private Integer durationDay;
}
