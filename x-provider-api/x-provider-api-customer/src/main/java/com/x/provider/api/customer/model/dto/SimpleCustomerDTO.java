package com.x.provider.api.customer.model.dto;

import com.x.provider.api.customer.enums.CustomerRelationEnum;
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
    private Integer gender;
    private CustomerStatDTO statistic;
    private Integer customerRelation = CustomerRelationEnum.NO_RELATION.getValue();
    @Builder.Default
    private Boolean canFollow = false;
}
