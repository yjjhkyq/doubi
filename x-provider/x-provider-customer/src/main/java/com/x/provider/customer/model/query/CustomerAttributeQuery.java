package com.x.provider.customer.model.query;

import com.x.core.domain.SuggestionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAttributeQuery {
    private List<Long> customerIdList;
    private SuggestionTypeEnum suggestionType;
}
