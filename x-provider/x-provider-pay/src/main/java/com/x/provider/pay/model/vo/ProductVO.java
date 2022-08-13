package com.x.provider.pay.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {
    @ApiModelProperty(value = "id", required = true)
    private Long id;
    @ApiModelProperty(value = "商品类型 1 金币 2 vip", required = true)
    private Integer productType;
    @ApiModelProperty(value = "商品价格", required = true)
    private Long price;
    @ApiModelProperty(value = "商品名称", required = true)
    private String name;
    @ApiModelProperty(value = "商品简要描述", required = true)
    private String shortDescription;
    @ApiModelProperty(value = "商品详细描述", required = true)
    private String fullDescription;
}

