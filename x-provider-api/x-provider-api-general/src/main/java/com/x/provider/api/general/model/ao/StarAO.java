package com.x.provider.api.general.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StarAO {
    private long associationItemId;
    private long itemId;
    private long starCustomerId;
    private int itemType;
    private boolean star;
}
