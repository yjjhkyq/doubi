package com.x.provider.api.statistic.model.ao;

import java.util.ArrayList;
import java.util.List;

public class ListMetricValueBatchAO {
    private List<ListMetricValueAO> conditions = new ArrayList<>();

    public List<ListMetricValueAO> getConditions() {
        return conditions;
    }

    public void setConditions(List<ListMetricValueAO> conditions) {
        this.conditions = conditions;
    }
}
