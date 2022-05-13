package com.x.provider.api.customer.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncCustomerStatAO {
    private Long id;
    @Builder.Default
    private Long followCount = 0L;
    @Builder.Default
    private Long fansCount = 0L;
    @Builder.Default
    private Long starCount = 0L;
}
