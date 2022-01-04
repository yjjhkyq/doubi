package com.x.provider.api.general.model.event;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent {
    private long id;
    private Long itemId;
    private Integer itemType;
    private Long itemCustomerId;
    private Long rootCommentId;
    private Long parentCommentId;
    private Long parentCommentCustomerId;
    private Long commentCustomerId;
    private String content;
    private Boolean authorComment;
}
