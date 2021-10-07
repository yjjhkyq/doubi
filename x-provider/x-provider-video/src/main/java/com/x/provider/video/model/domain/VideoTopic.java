package com.x.provider.video.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import io.swagger.models.auth.In;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("video_topic")
public class VideoTopic extends BaseEntity {
  @TableId
  private Long id;
  private Long topicId;
  private Long videoId;
  private Integer videoStatus;

}
