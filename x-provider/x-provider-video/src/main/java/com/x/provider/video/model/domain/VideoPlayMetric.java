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
@TableName("video_play_metric")
public class VideoPlayMetric extends BaseEntity {
  @TableId
  private long id;
  private long videoId;
  private long customerId;
  private int playDuration;
  private int videoDuration;
}
