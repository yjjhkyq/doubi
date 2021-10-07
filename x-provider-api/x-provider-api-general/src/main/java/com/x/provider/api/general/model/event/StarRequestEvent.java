package com.x.provider.api.general.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StarRequestEvent {
    private long associationItemId;
    private long itemId;
    private long starCustomerId;
    private int itemType;
    private boolean star;
}
