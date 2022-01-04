package com.x.provider.api.customer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCustomerDTO {
    private long id;
    private String userName;
    private String nickName;
    private String avatarUrl;
    private int relation;
    @Builder.Default
    private boolean canFollow = true;
}
