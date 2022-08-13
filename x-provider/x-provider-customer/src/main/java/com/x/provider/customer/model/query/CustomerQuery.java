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
public class CustomerQuery {
    private Long id;
    private List<Long> idList;
    private String userName;
    private String phone;
    private String email;
}
