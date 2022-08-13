package com.x.provider.customer.service.impl;

import com.x.provider.api.common.enums.ItemTypeEnum;
import com.x.provider.api.customer.model.event.FollowEvent;
import com.x.provider.api.statistic.enums.MetricEnum;
import com.x.provider.api.statistic.enums.PeriodEnum;
import com.x.provider.api.statistic.model.dto.IncMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.IncMetricValuesRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueBatchRequestDTO;
import com.x.provider.api.statistic.model.dto.MetricValueDTO;
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
        List<IncMetricValueRequestDTO> incMetricValueValuesAO = new ArrayList<>();
        if (customerStat.getFansCount() != 0){
            incMetricValueValuesAO.add(IncMetricValueRequestDTO.builder()
                    .itemId(String.valueOf(customerStat.getId()))
                    .itemType(ItemTypeEnum.CUSTOMER.getValue())
                    .metric(MetricEnum.FANS_COUNT.getValue())
                    .period(PeriodEnum.ALL.getValue())
                    .longValue(customerStat.getFansCount()).build());
        }
        if (customerStat.getFollowCount() != 0){
            incMetricValueValuesAO.add(IncMetricValueRequestDTO.builder()
                    .itemId(String.valueOf(customerStat.getId()))
                    .itemType(ItemTypeEnum.CUSTOMER.getValue())
                    .metric(MetricEnum.FOLLOW_COUNT.getValue())
                    .period(PeriodEnum.ALL.getValue())
                    .longValue(customerStat.getFollowCount()).build());
        }
        if (customerStat.getStarCount() != 0){
            incMetricValueValuesAO.add(IncMetricValueRequestDTO.builder()
                    .itemId(String.valueOf(customerStat.getId()))
                    .itemType(ItemTypeEnum.CUSTOMER.getValue())
                    .metric(MetricEnum.STAR_COUNT.getValue())
                    .period(PeriodEnum.ALL.getValue())
                    .longValue(customerStat.getStarCount()).build());
        }
        if (incMetricValueValuesAO.isEmpty()){
            return;
        }
        statisticTotalRpcService.incStatisticTotals(IncMetricValuesRequestDTO.builder().incMetricValues(incMetricValueValuesAO).build());
    }

    @Override
    public Map<Long, CustomerStat> list(List<Long> idList) {
        List<ListMetricValueRequestDTO> conditions = new ArrayList<>();
        conditions.add(ListMetricValueRequestDTO.builder()
                .itemIds(idList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemType(ItemTypeEnum.CUSTOMER.getValue())
                .metric(MetricEnum.FANS_COUNT.getValue())
                .period(PeriodEnum.ALL.getValue())
                .build());
        conditions.add(ListMetricValueRequestDTO.builder()
                .itemIds(idList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemType(ItemTypeEnum.CUSTOMER.getValue())
                .metric(MetricEnum.STAR_COUNT.getValue())
                .period(PeriodEnum.ALL.getValue())
                .build());
        conditions.add(ListMetricValueRequestDTO.builder()
                .itemIds(idList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemType(ItemTypeEnum.CUSTOMER.getValue())
                .metric(MetricEnum.FOLLOW_COUNT.getValue())
                .period(PeriodEnum.ALL.getValue())
                .build());
        ListMetricValueBatchRequestDTO listMetricValueBatchAO = new ListMetricValueBatchRequestDTO();
        listMetricValueBatchAO.setConditions(conditions);
        List<MetricValueDTO> metricValues = statisticTotalRpcService.listStatisticTotalBatch(listMetricValueBatchAO).getData();
        return prepare(metricValues);
    }

    @Override
    public void onFollowEvent(FollowEvent followEvent) {
        if (!Set.of(FollowEvent.EventTypeEnum.FOLLOW.getValue(), FollowEvent.EventTypeEnum.UN_FOLLOW.getValue()).contains(followEvent.getEventType())){
            return;
        }
        Long value = FollowEvent.EventTypeEnum.FOLLOW.getValue().equals(followEvent.getEventType()) ? 1L : -1L;
        List<IncMetricValueRequestDTO> incMetricValueValuesAO = new ArrayList<>();
        incMetricValueValuesAO.add(IncMetricValueRequestDTO.builder()
                .itemId(String.valueOf(followEvent.getFromCustomerId()))
                .itemType(ItemTypeEnum.CUSTOMER.getValue())
                .metric(MetricEnum.FANS_COUNT.getValue())
                .period(PeriodEnum.ALL.getValue())
                .longValue(value).build());
        incMetricValueValuesAO.add(IncMetricValueRequestDTO.builder()
                .itemId(String.valueOf(followEvent.getToCustomerId()))
                .itemType(ItemTypeEnum.CUSTOMER.getValue())
                .metric(MetricEnum.FOLLOW_COUNT.getValue())
                .period(PeriodEnum.ALL.getValue())
                .longValue(value)
                .build());
        statisticTotalRpcService.incStatisticTotals(IncMetricValuesRequestDTO.builder().incMetricValues(incMetricValueValuesAO).build());
    }

    @Override
    public CustomerStat getCustomerStat(Long customerId) {
        return list(Arrays.asList(customerId)).get(customerId);
    }

    private Map<Long, CustomerStat> prepare(List<MetricValueDTO> metricValues){
        if (metricValues.isEmpty()){
            return new HashMap<>();
        }
        Map<Long, CustomerStat> result = new HashMap<>(32);
        metricValues.stream().forEach(item -> {
            CustomerStat stat = result.getOrDefault(Long.valueOf(item.getItemId()), CustomerStat.builder().id(Long.valueOf(item.getItemId())).build());
            if (item.getMetric() == MetricEnum.STAR_COUNT.getValue()){
                stat.setStarCount(item.getLongValue());
            } else if (item.getMetric() == MetricEnum.FOLLOW_COUNT.getValue()){
                stat.setFollowCount(item.getLongValue());
            } else if (item.getMetric() == MetricEnum.FANS_COUNT.getValue()){
                stat.setFansCount(item.getLongValue());
            }
            result.putIfAbsent(stat.getId(), stat);
        });
        return result;
    }
}
