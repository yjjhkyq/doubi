package com.x.provider.customer.model.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericAttributeQuery {
    private String keyGroup;
    private Long entityId;
    private List<Long> entityIdList;
    private String key;
    private Integer suggestionType;
}
