package com.paascloud.provider.customer.model.vo;

import lombok.Data;

@Data
public class RoleVO {
    private long id;
    private String name;
    private boolean systemRole;
    private String systemName;
}
