package com.x.provider.api.general.model.ao;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentAO {
    private long id;
    private long itemId;
    private int itemType;
    private long itemCustomerId;
    private long parentCommentId;
    private long commentCustomerId;
    private String content;
}
