package com.x.provider.video.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("video_recommend_pool_hot_topic")
public class VideoRecommendPoolHotTopic extends BaseEntity {
  @TableId
  private Long id;
  private Long videoId;
  private Long topicId;
  private Long Score;
  private Date createOnDate;
}
