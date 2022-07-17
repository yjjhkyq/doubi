package com.x.provider.mc.model.query;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class GroupQuery {
    private Long id;
    private Long customerId;
    private Integer groupType;
}
