package com.x.provider.api.mc.model.protocol;

import lombok.Data;

@Data
public class ProductCommentInteractMsgBody extends InteractMsgBody {
    private boolean reply;
    private String commentContent;
    private Long commentId;
}
