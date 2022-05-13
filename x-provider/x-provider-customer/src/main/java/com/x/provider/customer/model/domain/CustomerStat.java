package com.x.provider.customer.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStat {
    private Long id;
    @Builder.Default
    private Long followCount = 0L;
    @Builder.Default
    private Long fansCount = 0L;
    @Builder.Default
    private Long starCount = 0L;
}
