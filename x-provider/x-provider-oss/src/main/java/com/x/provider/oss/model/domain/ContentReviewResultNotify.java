package com.x.provider.oss.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@TableName("content_review_result_notify")
public class ContentReviewResultNotify extends BaseEntity {
  @TableId
  private long id;
  private String fileId;
  private String notifyUrl;
  @TableField("is_notify_success")
  private boolean notifySuccess;
  private int retryCount;
}
