package com.x.provider.customer.model.ao;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SetCustomerAttributeAO {
    @NotBlank
    private String attributeName;
    @NotBlank
    private String value;
}
