package com.x.provider.vod.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@TableName("content_review_result")
public class ContentReviewResult extends BaseEntity {
  @TableId
  private long id;
  private String fileId;
  private String reviewType;
  private String suggestion;

  public ContentReviewResult(String fileId, String reviewType, String suggestion){
    this.fileId = fileId;
    this.reviewType = reviewType;
    this.suggestion = suggestion.toUpperCase();
  }
}
