package com.paascloud.provider.customer.model.vo;

import lombok.Data;

import java.util.Map;

@Data
public class FlowFansListItemVO {
    private long customerId;
    private Map<String, String> customerAttributes;
}
