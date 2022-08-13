package com.x.provider.api.customer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncCustomerStatRequestDTO {
    private Long id;
    @Builder.Default
    private Long followCount = 0L;
    @Builder.Default
    private Long fansCount = 0L;
    @Builder.Default
    private Long starCount = 0L;
}
