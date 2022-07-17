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
    private Long id;
    private String userName;
    private String nickName;
    private String avatarUrl;
    private CustomerRelationDTO customerRelation;
    private CustomerStatDTO statistic;
    private Boolean canFollow;
}
