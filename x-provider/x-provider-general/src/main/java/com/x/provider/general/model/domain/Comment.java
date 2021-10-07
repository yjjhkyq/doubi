package com.x.provider.general.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("comment")
public class Comment extends BaseEntity {
  @TableId
  private long id;
  private long itemId;
  private int itemType;
  private long commentCustomerId;
  private String content;
  private long replyCommentId;
  private long replyRootId;
}
