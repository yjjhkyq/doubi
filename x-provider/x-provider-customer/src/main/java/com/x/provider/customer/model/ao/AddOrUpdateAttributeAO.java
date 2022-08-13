package com.x.provider.customer.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: liushenyi
 * @date: 2022/07/26/10:17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddOrUpdateAttributeAO {
    private String keyGroup;
    private String key;
    private String value;
    private Integer suggestionType;
    private Long entityId;
}
