package com.x.provider.general.service.impl;

import com.x.provider.api.common.enums.ItemTypeEnum;
import com.x.provider.api.statistic.enums.MetricEnum;
import com.x.provider.api.statistic.enums.PeriodEnum;
import com.x.provider.api.statistic.model.ao.IncMetricValueValueAO;
import com.x.provider.api.statistic.model.ao.IncMetricValuesAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueAO;
import com.x.provider.api.statistic.model.ao.ListMetricValueBatchAO;
import com.x.provider.api.statistic.model.dto.ListMetricValueDTO;
import com.x.provider.api.statistic.service.StatisticTotalRpcService;
import com.x.provider.general.model.domain.Comment;
import com.x.provider.general.model.domain.CommentStatistic;
import com.x.provider.general.model.domain.Star;
import com.x.provider.general.service.CommentStatService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentStatServiceImpl implements CommentStatService {

    private final StatisticTotalRpcService statisticTotalRpcService;

    public CommentStatServiceImpl(StatisticTotalRpcService statisticTotalRpcService){
        this.statisticTotalRpcService = statisticTotalRpcService;
    }

    @Override
    public void onCommentInsert(Comment comment) {
        CommentStatistic commentStat = CommentStatistic.builder().id(comment.getRootCommentId()).build();
        if (comment.getRootCommentId() > 0){
            commentStat.setReplyCount(1L);
        }
        incCommentStat(commentStat);
    }

    @Override
    public void onStar(Star star) {
        if (!star.getItemType().equals(ItemTypeEnum.COMMENT.getValue())){
            return;
        }
        CommentStatistic commentStat = CommentStatistic.builder().id(star.getItemId()).starCount(star.isStar() ? 1 : -1).build();
        incCommentStat(commentStat);
    }

    @Override
    public Map<Long, CommentStatistic> listCommentStatMap(List<Long> idList) {
        List<ListMetricValueAO> conditions = new ArrayList<>();
        conditions.add(ListMetricValueAO.builder()
                .itemIds(idList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemTypeEnum(ItemTypeEnum.COMMENT.getValue())
                .metricEnum(MetricEnum.REPLY_COUNT.getValue())
                .periodEnum(PeriodEnum.ALL.getValue())
                .build());
        conditions.add(ListMetricValueAO.builder()
                .itemIds(idList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemTypeEnum(ItemTypeEnum.COMMENT.getValue())
                .metricEnum(MetricEnum.STAR_COUNT.getValue())
                .periodEnum(PeriodEnum.ALL.getValue())
                .build());
        ListMetricValueBatchAO listMetricValueBatchAO = new ListMetricValueBatchAO();
        listMetricValueBatchAO.setConditions(conditions);
        List<ListMetricValueDTO> metricValues = statisticTotalRpcService.listStatisticTotalBatch(listMetricValueBatchAO).getData();
        return prepare(metricValues);
    }

    public void incCommentStat(CommentStatistic commentStat){
        List<IncMetricValueValueAO> incMetricValueValuesAO = new ArrayList<>();
        if (commentStat.getReplyCount() != 0){
            incMetricValueValuesAO.add(IncMetricValueValueAO.builder()
                    .itemId(commentStat.getId().toString())
                    .itemTypeEnum(ItemTypeEnum.COMMENT.getValue())
                    .metricEnum(MetricEnum.REPLY_COUNT.getValue())
                    .periodEnum(PeriodEnum.ALL.getValue())
                    .longValue(commentStat.getReplyCount()).build());
        }
        if (commentStat.getStarCount() != 0){
            incMetricValueValuesAO.add(IncMetricValueValueAO.builder()
                    .itemId(commentStat.getId().toString())
                    .itemTypeEnum(ItemTypeEnum.COMMENT.getValue())
                    .metricEnum(MetricEnum.STAR_COUNT.getValue())
                    .periodEnum(PeriodEnum.ALL.getValue())
                    .longValue(commentStat.getReplyCount()).build());
        }
        if (incMetricValueValuesAO.isEmpty()){
            return;
        }
        statisticTotalRpcService.incStatisticTotals(IncMetricValuesAO.builder().incMetricValues(incMetricValueValuesAO).build());
    }

    private Map<Long, CommentStatistic> prepare(List<ListMetricValueDTO> metricValues){
        if (metricValues.isEmpty()){
            return new HashMap<>();
        }
        Map<Long, CommentStatistic> result = new HashMap<>(32);
        metricValues.stream().forEach(item -> {
            CommentStatistic commentStatistic = result.getOrDefault(Long.valueOf(item.getItemId()), CommentStatistic.builder().id(Long.valueOf(item.getItemId())).build());
            if (item.getMetricEnum() == MetricEnum.STAR_COUNT.getValue()){
                commentStatistic.setStarCount(item.getLongValue());
            }
            else if (item.getMetricEnum() == MetricEnum.REPLY_COUNT.getValue()){
                commentStatistic.setReplyCount(item.getLongValue());
            }
            result.putIfAbsent(commentStatistic.getId(), commentStatistic);
        });
        return result;
    }
}
