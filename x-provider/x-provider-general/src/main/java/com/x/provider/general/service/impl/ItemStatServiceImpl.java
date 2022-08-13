package com.x.provider.general.service.impl;

import com.x.provider.api.common.enums.ItemTypeEnum;
import com.x.provider.api.statistic.enums.MetricEnum;
import com.x.provider.api.statistic.enums.PeriodEnum;
import com.x.provider.api.statistic.model.dto.IncMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.IncMetricValuesRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueBatchRequestDTO;
import com.x.provider.api.statistic.model.dto.MetricValueDTO;
import com.x.provider.api.statistic.service.StatisticTotalRpcService;
import com.x.provider.general.model.domain.Comment;
import com.x.provider.general.model.domain.ItemStatistic;
import com.x.provider.general.model.domain.Star;
import com.x.provider.general.service.ItemStatService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemStatServiceImpl implements ItemStatService {

    private final StatisticTotalRpcService statisticTotalRpcService;

    public ItemStatServiceImpl(StatisticTotalRpcService statisticTotalRpcService){
        this.statisticTotalRpcService = statisticTotalRpcService;
    }

    @Override
    public void onCommentInsert(Comment comment) {
        ItemStatistic commentStat = ItemStatistic.builder().itemId(comment.getItemId()).itemType(comment.getItemType()).commentCount(1L).build();
        incItemStat(commentStat);
    }

    @Override
    public void onStar(Star star) {
        if (star.getItemType().equals(ItemTypeEnum.COMMENT.getValue())){
            return;
        }
        ItemStatistic commentStat = ItemStatistic.builder().itemId(star.getItemId()).itemType(star.getItemType()).starCount(star.isStar() ? 1 : -1).build();
        incItemStat(commentStat);
    }

    @Override
    public Map<Long, ItemStatistic> listItemStatMap(int itemType, List<Long> idList) {
        List<ListMetricValueRequestDTO> conditions = new ArrayList<>();
        conditions.add(ListMetricValueRequestDTO.builder()
                .itemIds(idList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemType(itemType)
                .metric(MetricEnum.COMMENT_COUNT.getValue())
                .period(PeriodEnum.ALL.getValue())
                .build());
        conditions.add(ListMetricValueRequestDTO.builder()
                .itemIds(idList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemType(itemType)
                .metric(MetricEnum.STAR_COUNT.getValue())
                .period(PeriodEnum.ALL.getValue())
                .build());
        ListMetricValueBatchRequestDTO listMetricValueBatchAO = new ListMetricValueBatchRequestDTO();
        listMetricValueBatchAO.setConditions(conditions);
        List<MetricValueDTO> metricValues = statisticTotalRpcService.listStatisticTotalBatch(listMetricValueBatchAO).getData();
        return prepare(metricValues);
    }

    public void incItemStat(ItemStatistic commentStat){
        List<IncMetricValueRequestDTO> incMetricValueValuesAO = new ArrayList<>();
        if (commentStat.getCommentCount() != 0){
            incMetricValueValuesAO.add(IncMetricValueRequestDTO.builder()
                    .itemId(commentStat.getItemId().toString())
                    .itemType(commentStat.getItemType())
                    .metric(MetricEnum.COMMENT_COUNT.getValue())
                    .period(PeriodEnum.ALL.getValue())
                    .longValue(commentStat.getCommentCount()).build());
        }
        if (commentStat.getStarCount() != 0){
            incMetricValueValuesAO.add(IncMetricValueRequestDTO.builder()
                    .itemId(commentStat.getItemId().toString())
                    .itemType(commentStat.getItemType())
                    .metric(MetricEnum.STAR_COUNT.getValue())
                    .period(PeriodEnum.ALL.getValue())
                    .longValue(commentStat.getCommentCount()).build());
        }
        if (incMetricValueValuesAO.isEmpty()){
            return;
        }
        statisticTotalRpcService.incStatisticTotals(IncMetricValuesRequestDTO.builder().incMetricValues(incMetricValueValuesAO).build());
    }

    private Map<Long, ItemStatistic> prepare(List<MetricValueDTO> metricValues){
        if (metricValues.isEmpty()){
            return new HashMap<>();
        }
        Map<Long, ItemStatistic> result = new HashMap<>(32);
        metricValues.stream().forEach(item -> {
            ItemStatistic itemStatistic = result.getOrDefault(Long.valueOf(item.getItemId()), ItemStatistic.builder().itemId(Long.valueOf(item.getItemId()))
                    .itemType(item.getItemType()).build());
            if (item.getMetric() == MetricEnum.STAR_COUNT.getValue()){
                itemStatistic.setStarCount(item.getLongValue());
            }
            else if (item.getItemType() == ItemTypeEnum.COMMENT.getValue() && item.getMetric() == MetricEnum.COMMENT_COUNT.getValue()){
                itemStatistic.setCommentCount(item.getLongValue());
            }
            result.putIfAbsent(itemStatistic.getItemId(), itemStatistic);
        });
        return result;
    }
}
