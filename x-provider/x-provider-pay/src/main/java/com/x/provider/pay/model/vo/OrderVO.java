package com.x.provider.pay.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.x.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class OrderVO {
  @ApiModelProperty(value = "订单id", required = true)
  private Long id;
  @ApiModelProperty(value = "订单编号", required = true)
  private String orderNo;
  @ApiModelProperty(value = "用户id", required = true)
  private Long customerId;
  @ApiModelProperty(value = "订单状态", required = true)
  private Integer orderStatus;
  @ApiModelProperty(value = "订单支付状态10 未支付 20 订单已撤销 30 未支付成功 35 支付中 40 已退款 50 订单已经关闭 60 支付错误", required = true)
  private Integer paymentStatus;
  @ApiModelProperty(value = "订单金额", required = true)
  private Long orderTotal;
  @ApiModelProperty(value = "订单支付日期，未支付订单此值为null")
  private Date paidDate;
  @ApiModelProperty(value = "订单创建日期", required = true)
  private Date createdOnUtc;

}
