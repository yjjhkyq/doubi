package com.x.provider.api.general.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentAO {
    private long itemId;
    private int itemType;
    private long commentCustomerId;
    private String content;
}
