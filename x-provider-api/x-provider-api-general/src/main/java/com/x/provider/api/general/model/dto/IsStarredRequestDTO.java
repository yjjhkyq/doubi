package com.x.provider.api.general.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsStarredRequestDTO {
    private int itemType;
    private long itemId;
    private long customerId;
}
