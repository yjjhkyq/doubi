package com.x.provider.api.statistic.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.statistic.model.dto.IncMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.IncMetricValuesRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueBatchRequestDTO;
import com.x.provider.api.statistic.model.dto.MetricValueDTO;
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
            public R<List<MetricValueDTO>> listStatisticTotalBatch(ListMetricValueBatchRequestDTO listStatisticTotalBatchAO) {
                return null;
            }

            @Override
            public R<List<MetricValueDTO>> listStatisticTotal(ListMetricValueRequestDTO listStatTotalAO) {
                return null;
            }

            @Override
            public R<Void> incStatisticTotal(IncMetricValueRequestDTO addStatisticTotalValueAO) {
                return null;
            }

            @Override
            public R<Void> incStatisticTotals(IncMetricValuesRequestDTO incStatisticTotalValuesAO) {
                return null;
            }
        };
    }
}
