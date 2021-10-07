package com.x.provider.api.statistic.model.ao;

import java.util.ArrayList;
import java.util.List;

public class ListStatisticTotalBatchAO {
    private List<ListStatTotalAO> conditions = new ArrayList<>();

    public List<ListStatTotalAO> getConditions() {
        return conditions;
    }

    public void setConditions(List<ListStatTotalAO> conditions) {
        this.conditions = conditions;
    }
}
