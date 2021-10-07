package com.x.provider.video.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("video")
public class Video extends BaseEntity {
  @TableId
  private Long id;
  private Long customerId;
  private String fileId;
  private String title;
  @TableField("is_reviewed")
  private Boolean reviewed;
  private Integer videoStatus;
  @TableField("is_top")
  private Boolean top;
  private Long topValue;

}
