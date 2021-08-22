package com.x.provider.customer.model.vo;

import lombok.Data;

@Data
public class CustomerRelationVO {
    private long fromCustomerId;
    private long toCustomerId;
    private int relation;
}
