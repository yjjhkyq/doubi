package com.x.provider.general.model.ao;

import com.x.core.web.page.PageDomain;
import io.swagger.annotations.ApiModel;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel
public class ListCommentAO{
    private long itemId;
    private int itemType;
    private Long rootCommentId;
    private PageDomain pageDomain;
}
