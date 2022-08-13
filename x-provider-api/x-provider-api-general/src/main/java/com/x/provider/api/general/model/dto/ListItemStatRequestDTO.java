package com.x.provider.api.general.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListItemStatRequestDTO {
    private int itemType;
    private List<Long> idList;
}
