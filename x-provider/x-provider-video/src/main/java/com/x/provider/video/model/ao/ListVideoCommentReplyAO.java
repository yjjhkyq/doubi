package com.x.provider.video.model.ao;

import com.x.core.web.page.PageDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListVideoCommentReplyAO extends PageDomain {
    private long videoId;
    private long replyCommentId;
}
