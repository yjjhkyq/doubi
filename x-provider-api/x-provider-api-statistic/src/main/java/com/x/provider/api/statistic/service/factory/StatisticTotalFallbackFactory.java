package com.x.provider.api.statistic.service.factory;

import com.x.core.web.api.R;
import com.x.provider.api.statistic.model.ao.IncStatisticTotalValueAO;
import com.x.provider.api.statistic.model.ao.ListStatTotalAO;
import com.x.provider.api.statistic.model.ao.ListStatisticTotalBatchAO;
import com.x.provider.api.statistic.model.dto.ListStatisticTotalDTO;
import com.x.provider.api.statistic.model.dto.ListStatisticTotalMapDTO;
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
            public R<ListStatisticTotalMapDTO> listStatisticTotalMap(ListStatisticTotalBatchAO listStatisticTotalAO) {
                return null;
            }

            @Override
            public R<List<ListStatisticTotalDTO>> listStatisticTotal(ListStatTotalAO listStatTotalAO) {
                return null;
            }

            @Override
            public R<Void> incStatisticTotal(IncStatisticTotalValueAO addStatisticTotalValueAO) {
                return null;
            }
        };
    }
}
