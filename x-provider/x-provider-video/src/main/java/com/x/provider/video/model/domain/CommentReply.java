package com.x.provider.video.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.x.core.domain.BaseEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("comment_reply")
public class CommentReply extends BaseEntity {
  @TableId
  private long id;
  private String itemId;
  private long commentId;
  private long commentCustomerId;
  private String content;
}
