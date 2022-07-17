package com.x.provider.customer.service.impl;

import com.x.provider.api.common.enums.ItemTypeEnum;
import com.x.provider.api.customer.model.event.FollowEvent;
import com.x.provider.api.statistic.enums.MetricEnum;
import com.x.provider.api.statistic.enums.PeriodEnum;
import com.x.provider.api.statistic.model.ao.IncMetricValueValueAO;
import com.x.provider.api.statistic.model.ao.IncMetricValuesAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueBatchAO;
import com.x.provider.api.statistic.model.dto.ListMetricValueDTO;
import com.x.provider.api.statistic.service.StatisticTotalRpcService;
import com.x.provider.customer.model.domain.CustomerStat;
import com.x.provider.customer.service.CustomerStatService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerStatServiceImpl implements CustomerStatService {

    private final StatisticTotalRpcService statisticTotalRpcService;

    public CustomerStatServiceImpl(StatisticTotalRpcService statisticTotalRpcService){
        this.statisticTotalRpcService = statisticTotalRpcService;
    }

    @Override
    public void inc(CustomerStat customerStat) {
        List<IncMetricValueValueAO> incMetricValueValuesAO = new ArrayList<>();
        if (customerStat.getFansCount() != 0){
            incMetricValueValuesAO.add(IncMetricValueValueAO.builder()
                    .itemId(String.valueOf(customerStat.getId()))
                    .itemTypeEnum(ItemTypeEnum.CUSTOMER.getValue())
                    .metricEnum(MetricEnum.FANS_COUNT.getValue())
                    .periodEnum(PeriodEnum.ALL.getValue())
                    .longValue(customerStat.getFansCount()).build());
        }
        if (customerStat.getFollowCount() != 0){
            incMetricValueValuesAO.add(IncMetricValueValueAO.builder()
                    .itemId(String.valueOf(customerStat.getId()))
                    .itemTypeEnum(ItemTypeEnum.CUSTOMER.getValue())
                    .metricEnum(MetricEnum.FOLLOW_COUNT.getValue())
                    .periodEnum(PeriodEnum.ALL.getValue())
                    .longValue(customerStat.getFollowCount()).build());
        }
        if (customerStat.getStarCount() != 0){
            incMetricValueValuesAO.add(IncMetricValueValueAO.builder()
                    .itemId(String.valueOf(customerStat.getId()))
                    .itemTypeEnum(ItemTypeEnum.CUSTOMER.getValue())
                    .metricEnum(MetricEnum.STAR_COUNT.getValue())
                    .periodEnum(PeriodEnum.ALL.getValue())
                    .longValue(customerStat.getStarCount()).build());
        }
        if (incMetricValueValuesAO.isEmpty()){
            return;
        }
        statisticTotalRpcService.incStatisticTotals(IncMetricValuesAO.builder().incMetricValues(incMetricValueValuesAO).build());
    }

    @Override
    public Map<Long, CustomerStat> list(List<Long> idList) {
        List<ListMetricValueAO> conditions = new ArrayList<>();
        conditions.add(ListMetricValueAO.builder()
                .itemIds(idList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemTypeEnum(ItemTypeEnum.CUSTOMER.getValue())
                .metricEnum(MetricEnum.FANS_COUNT.getValue())
                .periodEnum(PeriodEnum.ALL.getValue())
                .build());
        conditions.add(ListMetricValueAO.builder()
                .itemIds(idList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemTypeEnum(ItemTypeEnum.CUSTOMER.getValue())
                .metricEnum(MetricEnum.STAR_COUNT.getValue())
                .periodEnum(PeriodEnum.ALL.getValue())
                .build());
        conditions.add(ListMetricValueAO.builder()
                .itemIds(idList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemTypeEnum(ItemTypeEnum.CUSTOMER.getValue())
                .metricEnum(MetricEnum.FOLLOW_COUNT.getValue())
                .periodEnum(PeriodEnum.ALL.getValue())
                .build());
        ListMetricValueBatchAO listMetricValueBatchAO = new ListMetricValueBatchAO();
        listMetricValueBatchAO.setConditions(conditions);
        List<ListMetricValueDTO> metricValues = statisticTotalRpcService.listStatisticTotalBatch(listMetricValueBatchAO).getData();
        return prepare(metricValues);
    }

    @Override
    public void onFollowEvent(FollowEvent followEvent) {
        if (!Set.of(FollowEvent.EventTypeEnum.FOLLOW.getValue(), FollowEvent.EventTypeEnum.UN_FOLLOW.getValue()).contains(followEvent.getEventType())){
            return;
        }
        Long value = FollowEvent.EventTypeEnum.FOLLOW.getValue().equals(followEvent.getEventType()) ? 1L : -1L;
        List<IncMetricValueValueAO> incMetricValueValuesAO = new ArrayList<>();
        incMetricValueValuesAO.add(IncMetricValueValueAO.builder()
                .itemId(String.valueOf(followEvent.getFromCustomerId()))
                .itemTypeEnum(ItemTypeEnum.CUSTOMER.getValue())
                .metricEnum(MetricEnum.FANS_COUNT.getValue())
                .periodEnum(PeriodEnum.ALL.getValue())
                .longValue(value).build());
        incMetricValueValuesAO.add(IncMetricValueValueAO.builder()
                .itemId(String.valueOf(followEvent.getToCustomerId()))
                .itemTypeEnum(ItemTypeEnum.CUSTOMER.getValue())
                .metricEnum(MetricEnum.FOLLOW_COUNT.getValue())
                .periodEnum(PeriodEnum.ALL.getValue())
                .longValue(value)
                .build());
        statisticTotalRpcService.incStatisticTotals(IncMetricValuesAO.builder().incMetricValues(incMetricValueValuesAO).build());
    }

    @Override
    public CustomerStat getCustomerStat(Long customerId) {
        return list(Arrays.asList(customerId)).get(customerId);
    }

    private Map<Long, CustomerStat> prepare(List<ListMetricValueDTO> metricValues){
        if (metricValues.isEmpty()){
            return new HashMap<>();
        }
        Map<Long, CustomerStat> result = new HashMap<>(32);
        metricValues.stream().forEach(item -> {
            CustomerStat stat = result.getOrDefault(Long.valueOf(item.getItemId()), CustomerStat.builder().id(Long.valueOf(item.getItemId())).build());
            if (item.getMetricEnum() == MetricEnum.STAR_COUNT.getValue()){
                stat.setStarCount(item.getLongValue());
            } else if (item.getMetricEnum() == MetricEnum.FOLLOW_COUNT.getValue()){
                stat.setFollowCount(item.getLongValue());
            } else if (item.getMetricEnum() == MetricEnum.FANS_COUNT.getValue()){
                stat.setFansCount(item.getLongValue());
            }
            result.putIfAbsent(stat.getId(), stat);
        });
        return result;
    }
}
