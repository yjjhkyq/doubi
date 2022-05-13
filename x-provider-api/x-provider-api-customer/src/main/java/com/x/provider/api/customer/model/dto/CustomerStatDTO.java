package com.x.provider.api.customer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatDTO {
    private Long id;
    private Long followCount = 0L;
    private Long fansCount = 0L;
    private Long starCount = 0L;
}
