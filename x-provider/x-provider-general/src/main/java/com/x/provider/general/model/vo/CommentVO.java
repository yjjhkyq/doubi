package com.x.provider.general.model.vo;

import com.x.provider.api.customer.model.dto.SimpleCustomerDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class CommentVO {
  private long id;
  @ApiModelProperty(value = "评论用户信息")
  private SimpleCustomerDTO commentCustomer;
  @ApiModelProperty(value = "回复人用户信息， commentCustomer 回复了 replyCustomer 的评论")
  private SimpleCustomerDTO parentCommentCustomer;
  @ApiModelProperty(value = "评论内容")
  private String content;
  @ApiModelProperty(value = "true 作者评论 反之 false")
  private boolean authorComment;
  @ApiModelProperty(value = "评论创建日期")
  private Date createdOnUtc;
  @ApiModelProperty(value = "评论统计信息")
  private CommentStatisticVO statistic;
}
