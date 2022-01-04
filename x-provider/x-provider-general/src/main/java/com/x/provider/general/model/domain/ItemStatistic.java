package com.x.provider.general.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemStatistic {
    private Long itemId;
    private Integer itemType;
    private long commentCount;
    private long starCount;
}
