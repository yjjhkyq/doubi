package com.x.provider.api.customer.model.dto;

import com.x.core.domain.SuggestionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListCustomerRequestDTO {
    @NotNull
    private List<Long> customerIds;
    @Builder.Default
    private List<String> customerOptions = new ArrayList<>();
    @Builder.Default
    private Long sessionCustomerId = 0L;

    private SuggestionTypeEnum suggestionType = null;
}
