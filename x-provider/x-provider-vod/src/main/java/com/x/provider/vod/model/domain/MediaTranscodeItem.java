package com.x.provider.vod.model.domain;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("media_transcode_item")
public class MediaTranscodeItem extends BaseEntity {
  @TableId
  private long id;
  private String fileId;
  private String url;
  private long height;
  private long width;
  private long size;
  private double duration;
  private String container;
}
