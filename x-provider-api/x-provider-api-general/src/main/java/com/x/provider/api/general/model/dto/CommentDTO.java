package com.x.provider.api.general.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private long id;
    private String itemId;
    private int itemType;
    private long commentCustomerId;
    private String commentCustomerNickName;
    private String content;
    private long replyCommentId;
    private long replyRootId;
    private long replyCustomerId;
    private String replyCustomerNickName;
}
