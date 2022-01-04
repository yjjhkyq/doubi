package com.x.provider.general.model.domain;

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
@TableName("comment")
public class Comment extends BaseEntity {
  @TableId
  private long id;
  private Long itemId;
  private Integer itemType;
  private Long itemCustomerId;
  private Long rootCommentId;
  private Long parentCommentId;
  private Long parentCommentCustomerId;
  private Long commentCustomerId;
  private String content;
  @TableField("is_author_comment")
  private Boolean authorComment;
}
