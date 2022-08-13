package com.x.provider.mc.model.query;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class MessageQuery {
    private Long id;
    private Long fromCustomerId;
    private Long toCustomerId;
    private Long toGroupId;
    private Long gtId;
    private Long ltId;
    private Date gtCreateDate;
    private List<Long> idList;
}
