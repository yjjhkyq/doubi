package com.x.provider.api.general.model.event;

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
    private Long commentCustomerId;
    private String commentCustomerNickName;
    private String content;
    private Long replyCommentId;
    private Long replyRootId;
    private Long replyCustomerId;
    private String replyCustomerNickName;
}
