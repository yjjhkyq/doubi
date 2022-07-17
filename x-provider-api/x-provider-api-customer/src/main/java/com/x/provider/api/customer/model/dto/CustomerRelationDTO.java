package com.x.provider.api.customer.model.dto;

import lombok.Data;

@Data
public class CustomerRelationDTO {
    private Long id;

    private Long fromCustomerId;

    private Long toCustomerId;

    private Boolean follow;

    private Boolean friend;
}
