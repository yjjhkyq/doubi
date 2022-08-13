package com.x.provider.api.customer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListSimpleCustomerRequestDTO {
    private Long sessionCustomerId;
    private List<Long> customerIds = new ArrayList<>();
    private List<String> customerOptions = new ArrayList<>();

    public List<String> getCustomerOptions() {
        return customerOptions == null ? new ArrayList<>() : customerOptions;
    }

    public List<Long> getCustomerIds() {
        return customerIds == null ? new ArrayList<>() : customerIds;
    }
}
