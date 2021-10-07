package com.x.provider.api.general.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StarDTO {
    private long id;
    private long associationItemId;
    private long itemId;
    private long starCustomerId;
    private boolean isStar;
    private int itemType;
}
