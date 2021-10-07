package com.x.provider.api.general.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsStarredAO {
    private int itemType;
    private long itemId;
    private long customerId;
}
