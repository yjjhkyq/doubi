package com.x.provider.pay.model.domain.product;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("product")
public class Product extends BaseEntity {
    @TableId
    private Long id;
    private Integer productType;
    private Long price;
    private String name;
    private String shortDescription;
    private String fullDescription;
    private Integer displayOrder;
}

