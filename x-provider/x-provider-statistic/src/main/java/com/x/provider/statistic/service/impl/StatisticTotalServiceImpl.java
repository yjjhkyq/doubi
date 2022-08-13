package com.x.provider.statistic.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.x.core.utils.BeanUtil;
import com.x.core.utils.DateUtils;
import com.x.provider.api.statistic.constants.StatisticEventTopic;
import com.x.provider.api.statistic.enums.PeriodEnum;
import com.x.provider.api.statistic.model.dto.ListMetricValueBatchRequestDTO;
import com.x.provider.api.statistic.model.dto.ListMetricValueRequestDTO;
import com.x.provider.api.statistic.model.event.MetricValueChangedEvent;
import com.x.provider.api.statistic.model.event.IncMetricValueEvent;
import com.x.provider.statistic.enums.StatisticTotalChangedNotifyItemEnum;
import com.x.provider.statistic.mapper.StatisticTotalMapper;
import com.x.provider.statistic.model.bo.IncStatisticTotalBO;
import com.x.provider.statistic.model.domain.StatisticTotal;
import com.x.provider.statistic.model.query.StatisticTotalQuery;
import com.x.provider.statistic.service.RedisKeyService;
import com.x.provider.statistic.service.StatisticTotalKeyService;
import com.x.provider.statistic.service.StatisticTotalService;
import com.x.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Service
public class StatisticTotalServiceImpl implements StatisticTotalService {

    private final RedisKeyService redisKeyService;
    private final RedisService redisService;
    private final StatisticTotalKeyService statisticTotalKeyService;
    private final StringRedisTemplate stringRedisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final StatisticTotalMapper statisticTotalMapper;

    public StatisticTotalServiceImpl(RedisKeyService redisKeyService,
                                     RedisService redisService,
                                     StatisticTotalKeyService statisticTotalKeyService,
                                     StringRedisTemplate stringRedisTemplate,
                                     KafkaTemplate<String, Object> kafkaTemplate,
                                     StatisticTotalMapper statisticTotalMapper){
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
        this.statisticTotalKeyService = statisticTotalKeyService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.statisticTotalMapper = statisticTotalMapper;
    }

    @Override
    public void onStatTotal(IncMetricValueEvent statisticTotalEvent) {
        incStatTotal(BeanUtil.prepare(statisticTotalEvent, StatisticTotal.class));
    }

    @Override
    public void incStatTotal(StatisticTotal statisticTotal){
        incStatisticTotalDb(statisticTotal);
        final StatisticTotal incStatisticTotalResult = incStatisticTotalRedis(statisticTotal);
        if (StatisticTotalChangedNotifyItemEnum.valeOf(statisticTotal.getMetric(), statisticTotal.getPeriod(), statisticTotal.getItemType()) != null){
            kafkaTemplate.send(StatisticEventTopic.TOPIC_NAME_STAT_METRIC_CHANGED_EVENT, StrUtil.format("{}:{}:{}", statisticTotal.getPeriod(),
                    statisticTotal.getMetric(), statisticTotal.getItemType()), new MetricValueChangedEvent(
                            statisticTotal.getMetric(), statisticTotal.getPeriod(), statisticTotal.getItemType(), statisticTotal.getItemId(),
                    incStatisticTotalResult.getDoubleValue(), incStatisticTotalResult.getLongValue()));
        }
    }

    @Override
    public void incStatTotals(List<StatisticTotal> statisticTotals) {
        statisticTotals.forEach(item ->{
            incStatTotal(item);
        });
    }

    @Override
    public List<StatisticTotal> listStatisticTotal(ListMetricValueRequestDTO listStatTotalAO) {
        return listStatisticTotal(Arrays.asList(listStatTotalAO));
    }

    @Override
    public List<StatisticTotal> listStatisticTotalBatch(ListMetricValueBatchRequestDTO listStatisticTotalBatchAO) {
        return listStatisticTotal(listStatisticTotalBatchAO.getConditions());
    }

    private StatisticTotal incStatisticTotalDb(StatisticTotal statisticTotal){
        final StatisticTotalQuery query = BeanUtil.prepare(statisticTotal, StatisticTotalQuery.class);
        if (Objects.equals(PeriodEnum.ALL.getValue(), query.getPeriod())){
            statisticTotal.setStartDate(formatStartDate(statisticTotal.getPeriod(), statisticTotal.getStartDate()));
            query.setStartDate(statisticTotal.getStartDate());
        }
        StatisticTotal statisticTotalEntity = get(query);
        if (statisticTotalEntity == null){
            statisticTotalEntity = BeanUtil.prepare(statisticTotal, StatisticTotal.class);
            statisticTotalMapper.insert(statisticTotalEntity);
        }
        else{
            statisticTotalMapper.incValue(IncStatisticTotalBO.builder().id(statisticTotalEntity.getId()).longValue(statisticTotal.getLongValue())
                    .doubleValue(statisticTotal.getDoubleValue()).build());
            statisticTotalEntity.setDoubleValue(statisticTotalEntity.getDoubleValue() + statisticTotal.getDoubleValue());
            statisticTotalEntity.setLongValue(statisticTotalEntity.getLongValue() + statisticTotal.getLongValue());
        }
        return statisticTotalEntity;
    }

    private StatisticTotal incStatisticTotalRedis(StatisticTotal statisticTotal){
        StatisticTotal result = BeanUtil.prepare(statisticTotal, StatisticTotal.class);
        Pair<String, String> keyFieldPair = statisticTotalKeyService.packageKey(statisticTotal);
        String redisKey = redisKeyService.getStatisticTotalKey(keyFieldPair.getFirst());

        if (statisticTotal.getLongValue() != null && statisticTotal.getLongValue() >0) {
            result.setLongValue(redisService.incr(redisKey, keyFieldPair.getSecond(), statisticTotal.getLongValue()));
        }
        else if (statisticTotal.getDoubleValue() != null){
            result.setDoubleValue(redisService.incr(redisKey, keyFieldPair.getSecond(), statisticTotal.getDoubleValue()));
        }
        else {
            log.error("no any value find for stat total, stat total item name:{}, statistic period:{}", statisticTotal.getMetric(), statisticTotal.getPeriod());
            throw new IllegalStateException(StrUtil.format("no any value find for stat total, stat total item name:{}, statistic period:{}", statisticTotal.getMetric(), statisticTotal.getPeriod()));
        }
        return result;
    }

    private List<StatisticTotal> listStatisticTotal(List<ListMetricValueRequestDTO> listStatTotalAOS) {
        List<StatisticTotal> result = new ArrayList<>();
        List<Object> cacheResult = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            listStatTotalAOS.forEach(listStatTotalAO -> {
                listStatTotalAO.getItemIds().forEach(item -> {
                    StatisticTotal statisticTotal = StatisticTotal.builder().startDate(listStatTotalAO.getDate())
                            .itemType(listStatTotalAO.getItemType()).itemId(item)
                            .period(listStatTotalAO.getPeriod()).metric(listStatTotalAO.getMetric()).startDate(listStatTotalAO.getDate()).build();
                    result.add(statisticTotal);
                    Pair<String, String> keyField = statisticTotalKeyService.packageKey(statisticTotal);
                    connection.hMGet(redisKeyService.getStatisticTotalKey(keyField.getFirst()).getBytes(), keyField.getSecond().getBytes());
                });
            });
            return null;
        });
        return prepare(cacheResult, result);
    }

    private ArrayList<StatisticTotal> prepare(List<Object> values, List<StatisticTotal> result){
        ArrayList<StatisticTotal> prepareResult = new ArrayList<>(result.size());
        if (values != null){
            for (int i = 0, j = values.size(); i < j; i ++){
                if (values.get(i) == null || CollectionUtils.isEmpty((List)values.get(i))){
                    continue;
                }
                Object value = ((List)values.get(i)).get(0);
                if (value == null){
                    continue;
                }
                result.get(i).setDoubleValue(Double.valueOf(value.toString()));
                result.get(i).setLongValue((result.get(i).getDoubleValue().longValue()));
                prepareResult.add(result.get(i));
            }
        }
        return prepareResult;
    }

    private StatisticTotal get(StatisticTotalQuery query){
        return statisticTotalMapper.selectOne(buildQuery(query));
    }

    private LambdaQueryWrapper<StatisticTotal> buildQuery(StatisticTotalQuery statisticTotalQuery){
        LambdaQueryWrapper<StatisticTotal> query = new LambdaQueryWrapper<>();
        if (statisticTotalQuery.getId() != null){
            query = query.eq(StatisticTotal::getId, statisticTotalQuery.getId());
        }
        if (!StringUtils.isEmpty(statisticTotalQuery.getItemId())){
            query = query.eq(StatisticTotal::getItemId, statisticTotalQuery.getItemId());
        }
        if (statisticTotalQuery.getItemType() != null){
            query = query.eq(StatisticTotal::getItemType, statisticTotalQuery.getItemType());
        }
        if (statisticTotalQuery.getMetric() != null){
            query = query.eq(StatisticTotal::getMetric, statisticTotalQuery.getMetric());
        }
        if (statisticTotalQuery.getPeriod() != null){
            query = query.eq(StatisticTotal::getPeriod, statisticTotalQuery.getPeriod());
        }
        if (statisticTotalQuery.getStartDate() != null){
            query = query.eq(StatisticTotal::getStartDate, statisticTotalQuery.getStartDate());
        }
        if (!CollectionUtils.isEmpty(statisticTotalQuery.getItemIdList())){
            query = query.in(StatisticTotal::getItemId, statisticTotalQuery.getItemIdList());
        }
        return query;
    }

    private Date formatStartDate(Integer period, Date startDate){
        PeriodEnum statisticPeriodEnum = PeriodEnum.valueOf(period);
        switch (statisticPeriodEnum){
            case ALL:
                return DateUtils.minDate();
            default:
                return startDate;
        }
    }
}
