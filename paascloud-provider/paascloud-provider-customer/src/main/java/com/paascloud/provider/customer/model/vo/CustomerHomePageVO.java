package com.paascloud.provider.customer.model.vo;

import lombok.Data;

import java.util.Map;

@Data
public class CustomerHomePageVO {
    private long id;
    private String userName;
    private Map<String, String> attributes;
}
