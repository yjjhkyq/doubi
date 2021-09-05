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
  private long id;
  private long customerId;
  private String fileId;
  private String title;
  @TableField("is_reviewed")
  private boolean reviewed;
  private int videoStatus;

}
