package com.x.provider.customer.model.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListCustomerRelationQuery {
    private Long fromCustomerId;
    private Long toCustomerId;
    private List<Long> toCustomerIdList;
    private List<Long> fromCustomerIdList;
    private Boolean follow;
    private Boolean friend;
}
