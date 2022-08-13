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
        List<ListMetricValueRequestDTO> conditions = new ArrayList<>();
        conditions.add(ListMetricValueRequestDTO.builder()
                .itemIds(idList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemType(ItemTypeEnum.COMMENT.getValue())
                .metric(MetricEnum.COMMENT_REPLY_COUNT.getValue())
                .period(PeriodEnum.ALL.getValue())
                .build());
        conditions.add(ListMetricValueRequestDTO.builder()
                .itemIds(idList.stream().map(String::valueOf).collect(Collectors.toList()))
                .itemType(ItemTypeEnum.COMMENT.getValue())
                .metric(MetricEnum.STAR_COUNT.getValue())
                .period(PeriodEnum.ALL.getValue())
                .build());
        ListMetricValueBatchRequestDTO listMetricValueBatchAO = new ListMetricValueBatchRequestDTO();
        listMetricValueBatchAO.setConditions(conditions);
        List<MetricValueDTO> metricValues = statisticTotalRpcService.listStatisticTotalBatch(listMetricValueBatchAO).getData();
        return prepare(metricValues);
    }

    public void incCommentStat(CommentStatistic commentStat){
        List<IncMetricValueRequestDTO> incMetricValueValuesAO = new ArrayList<>();
        if (commentStat.getReplyCount() != 0){
            incMetricValueValuesAO.add(IncMetricValueRequestDTO.builder()
                    .itemId(commentStat.getId().toString())
                    .itemType(ItemTypeEnum.COMMENT.getValue())
                    .metric(MetricEnum.COMMENT_REPLY_COUNT.getValue())
                    .period(PeriodEnum.ALL.getValue())
                    .longValue(commentStat.getReplyCount()).build());
        }
        if (commentStat.getStarCount() != 0){
            incMetricValueValuesAO.add(IncMetricValueRequestDTO.builder()
                    .itemId(commentStat.getId().toString())
                    .itemType(ItemTypeEnum.COMMENT.getValue())
                    .metric(MetricEnum.STAR_COUNT.getValue())
                    .period(PeriodEnum.ALL.getValue())
                    .longValue(commentStat.getReplyCount()).build());
        }
        if (incMetricValueValuesAO.isEmpty()){
            return;
        }
        statisticTotalRpcService.incStatisticTotals(IncMetricValuesRequestDTO.builder().incMetricValues(incMetricValueValuesAO).build());
    }

    private Map<Long, CommentStatistic> prepare(List<MetricValueDTO> metricValues){
        if (metricValues.isEmpty()){
            return new HashMap<>();
        }
        Map<Long, CommentStatistic> result = new HashMap<>(32);
        metricValues.stream().forEach(item -> {
            CommentStatistic commentStatistic = result.getOrDefault(Long.valueOf(item.getItemId()), CommentStatistic.builder().id(Long.valueOf(item.getItemId())).build());
            if (item.getMetric() == MetricEnum.STAR_COUNT.getValue()){
                commentStatistic.setStarCount(item.getLongValue());
            }
            else if (item.getMetric() == MetricEnum.COMMENT_REPLY_COUNT.getValue()){
                commentStatistic.setReplyCount(item.getLongValue());
            }
            result.putIfAbsent(commentStatistic.getId(), commentStatistic);
        });
        return result;
    }
}
