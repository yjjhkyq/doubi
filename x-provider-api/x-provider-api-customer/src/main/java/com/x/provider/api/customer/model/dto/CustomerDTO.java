package com.x.provider.api.customer.model.dto;

import com.x.provider.api.customer.enums.CustomerRelationEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String userName;
    private String email;
    private Boolean active;
    private Boolean systemAccount;
    private Date createdOnUtc;
    private Date updatedOnUtc;
    private String phone;

    private CustomerAttributeDTO customerAttribute;

    @Builder.Default
    private List<GenericAttributeDTO> customerAttributeList = new ArrayList<>();

    private CustomerStatDTO statistic;

    @Builder.Default
    private Integer customerRelation = CustomerRelationEnum.NO_RELATION.getValue();
    private Boolean canFollow;

}
