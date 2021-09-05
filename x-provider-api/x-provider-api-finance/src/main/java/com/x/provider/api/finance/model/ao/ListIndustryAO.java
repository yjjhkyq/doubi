package com.x.provider.api.finance.model.ao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListIndustryAO {
    private List<Long> ids;
    private Date updateOnUtcAfter;
}
