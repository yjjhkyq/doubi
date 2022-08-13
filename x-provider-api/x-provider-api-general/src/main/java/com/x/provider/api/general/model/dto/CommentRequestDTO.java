package com.x.provider.api.general.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    private long id;
    private long itemId;
    private int itemType;
    private long itemCustomerId;
    private long parentCommentId;
    private long commentCustomerId;
    private String content;
}
