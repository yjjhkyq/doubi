package com.x.provider.customer.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCustomerVO {
    private long id;
    private String userName;
    private String nickName;
    private String avatarUrl;
    private int relation;
}

