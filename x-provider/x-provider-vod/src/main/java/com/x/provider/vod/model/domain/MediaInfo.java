package com.x.provider.vod.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("media_info")
public class MediaInfo extends BaseEntity {
  @TableId
  private long id;
  private String fileId;
  private String coverUrl;
  private String type;
  private String mediaUrl;
  private long size;
  private long height;
  private long width;
  private double duration;
  private String name;
  private String description;
}
