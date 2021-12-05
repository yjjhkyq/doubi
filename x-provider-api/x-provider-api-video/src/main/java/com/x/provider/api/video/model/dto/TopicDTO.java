package com.x.provider.api.video.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO{
  private Long id;
  private String title;
  private Integer effectValue;
  private Integer sourceType;
  private String searchKeyWord;
  private String sourceId;
  private String topicDescription;
  private Boolean systemTopic;
}
