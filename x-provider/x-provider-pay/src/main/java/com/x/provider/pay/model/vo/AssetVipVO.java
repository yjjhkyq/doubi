package com.x.provider.pay.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class AssetVipVO{
    @ApiModelProperty(value = "id", required = true)
    private Long id;
    @ApiModelProperty(value = "用户id", required = true)
    private Long customerId;
    @ApiModelProperty(value = "vip过期日期, 为null表示永久有效")
    private Date expireDate;
}

