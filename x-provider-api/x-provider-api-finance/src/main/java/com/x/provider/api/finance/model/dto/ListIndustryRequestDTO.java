package com.x.provider.api.finance.model.dto;

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
public class ListIndustryRequestDTO {
    private List<Long> ids;
    private Date updateOnUtcAfter;
}
