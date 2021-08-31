package com.x.provider.api.finance.model.ao;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ListSecurityAO {
    private List<Long> ids;
    private Date updateOnUtcAfter;
}
