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
public class ListStarAO extends PageDomain {
    private long associationItemId;
    private  long starCustomerId;
    private int itemType;
}
