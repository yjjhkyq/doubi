package com.x.provider.api.statistic.model.dto;

import java.util.ArrayList;
import java.util.List;

public class ListMetricValueBatchRequestDTO {
    private List<ListMetricValueRequestDTO> conditions = new ArrayList<>();

    public List<ListMetricValueRequestDTO> getConditions() {
        return conditions;
    }

    public void setConditions(List<ListMetricValueRequestDTO> conditions) {
        this.conditions = conditions;
    }
}
