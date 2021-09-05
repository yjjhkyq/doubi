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
@TableName("topic")
public class Topic extends BaseEntity {
  @TableId
  private long id;
  private String title;
  private int effectValue;
  private int sourceType;
  private String searchKeyWord;
  private String sourceId;
  private String topicDescription;
  @TableField("is_system_topic")
  private boolean systemTopic;

}
