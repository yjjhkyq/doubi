package com.x.provider.api.statistic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncMetricValuesRequestDTO {
    private List<IncMetricValueRequestDTO> incMetricValues;
}
