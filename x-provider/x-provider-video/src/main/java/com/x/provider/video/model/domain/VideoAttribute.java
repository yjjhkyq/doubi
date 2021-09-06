package com.x.provider.video.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("video_attribute")
public class VideoAttribute extends BaseEntity {
  @TableId(type = IdType.INPUT)
  private long videoId;
  @TableField("is_top")
  private boolean top;
  private long topValue;

}
