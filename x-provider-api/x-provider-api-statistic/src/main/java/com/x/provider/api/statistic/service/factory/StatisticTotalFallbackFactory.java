package com.x.provider.api.statistic.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.statistic.model.ao.IncMetricValueValueAO;
import com.x.provider.api.statistic.model.ao.IncMetricValuesAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueBatchAO;
import com.x.provider.api.statistic.model.dto.ListMetricValueDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueMapDTO;
import com.x.provider.api.statistic.service.StatisticTotalRpcService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatisticTotalFallbackFactory implements FallbackFactory<StatisticTotalRpcService> {

    @Override
    public StatisticTotalRpcService create(Throwable throwable) {
        return new StatisticTotalRpcService() {
            @Override
            public R<ListMetricValueMapDTO> listStatisticTotalMap(ListMetricValueBatchAO listStatisticTotalAO) {
                return null;
            }

            @Override
            public R<List<ListMetricValueDTO>> listStatisticTotalBatch(ListMetricValueBatchAO listStatisticTotalBatchAO) {
                return null;
            }

            @Override
            public R<List<ListMetricValueDTO>> listStatisticTotal(ListMetricValueAO listStatTotalAO) {
                return null;
            }

            @Override
            public R<Void> incStatisticTotal(IncMetricValueValueAO addStatisticTotalValueAO) {
                return null;
            }

            @Override
            public R<Void> incStatisticTotals(IncMetricValuesAO incStatisticTotalValuesAO) {
                return null;
            }
        };
    }
}
