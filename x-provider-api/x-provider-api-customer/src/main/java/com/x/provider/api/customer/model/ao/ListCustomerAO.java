package com.x.provider.api.customer.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListCustomerAO {
    private List<Long> customerIds;
    private List<String> customerOptions;
}
