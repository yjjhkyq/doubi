package com.x.provider.api.general.model.ao;

import com.x.core.web.page.PageDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListCommentAO{
    private long itemId;
    private int itemType;
    private long commentCustomerId;
    long replyCommentId;
    private long replyRootId;
    private PageDomain pageDomain;
}
