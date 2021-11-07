package com.x.provider.statistic.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.core.utils.BeanUtil;
import com.x.provider.api.statistic.constants.StatisticEventTopic;
import com.x.provider.api.statistic.model.ao.ListStatisticTotalBatchAO;
import com.x.provider.api.statistic.model.ao.ListStatTotalAO;
import com.x.provider.api.statistic.model.dto.ListStatisticTotalDTO;
import com.x.provider.api.statistic.model.dto.ListStatisticTotalMapDTO;
import com.x.provider.api.statistic.model.event.StatisticTotalChangedEvent;
import com.x.provider.api.statistic.model.event.StatisticTotalEvent;
import com.x.provider.statistic.enums.StatisticTotalChangedNotifyItemEnum;
import com.x.provider.statistic.model.domain.StatisticTotal;
import com.x.provider.statistic.service.RedisKeyService;
import com.x.provider.statistic.service.StatisticTotalKeyService;
import com.x.provider.statistic.service.StatisticTotalService;
import com.x.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class StatisticTotalServiceImpl implements StatisticTotalService {

    private final RedisKeyService redisKeyService;
    private final RedisService redisService;
    private final StatisticTotalKeyService statisticTotalKeyService;
    private final RedisTemplate redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public StatisticTotalServiceImpl(RedisKeyService redisKeyService,
                                     RedisService redisService,
                                     StatisticTotalKeyService statisticTotalKeyService,
                                     RedisTemplate redisTemplate,
                                     KafkaTemplate<String, Object> kafkaTemplate){
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
        this.statisticTotalKeyService = statisticTotalKeyService;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void onStatTotal(StatisticTotalEvent statisticTotalEvent) {
        incStatTotal(BeanUtil.prepare(statisticTotalEvent, StatisticTotal.class));
    }

    public void incStatTotal(StatisticTotal statisticTotal){
        Pair<String, String> keyFieldPair = statisticTotalKeyService.packageKey(statisticTotal);
        String redisKey = redisKeyService.getStatisticTotalKey(keyFieldPair.getFirst());

        Long newValue = null;
        Double newDoubleValue = null;

        if (statisticTotal.getLongValue() != null && statisticTotal.getLongValue() >0) {
            newValue = redisService.incr(redisKey, keyFieldPair.getSecond(), statisticTotal.getLongValue());
        }
        else if (statisticTotal.getDoubleValue() != null){
            newDoubleValue = redisService.incr(redisKey, keyFieldPair.getSecond(), statisticTotal.getDoubleValue());
        }
        else {
            log.error("no any value find for stat total, stat total item name:{}, statistic period:{}", statisticTotal.getStatTotalItemNameEnum(), statisticTotal.getStatisticPeriodEnum());
            throw new IllegalStateException(StrUtil.format("no any value find for stat total, stat total item name:{}, statistic period:{}", statisticTotal.getStatTotalItemNameEnum(), statisticTotal.getStatisticPeriodEnum()));
        }
        if (StatisticTotalChangedNotifyItemEnum.valeOf(statisticTotal.getStatTotalItemNameEnum(), statisticTotal.getStatisticPeriodEnum(), statisticTotal.getStatisticObjectClassEnum()) != null){
            kafkaTemplate.send(StatisticEventTopic.TOPIC_NAME_STAT_TOTAL_CHANGED_EVENT, StrUtil.format("{}:{}:{}", statisticTotal.getStatisticPeriodEnum(),
                    statisticTotal.getStatTotalItemNameEnum(), statisticTotal.getStatisticObjectClassEnum()), new StatisticTotalChangedEvent(
                            statisticTotal.getStatTotalItemNameEnum(), statisticTotal.getStatisticPeriodEnum(), statisticTotal.getStatisticObjectClassEnum(), statisticTotal.getStatisticObjectId(),
                    newDoubleValue, newValue));
        }
    }

    @Override
    public ListStatisticTotalMapDTO listStatisticTotalMap(ListStatisticTotalBatchAO listStatisticTotalAO) {
        return null;
    }

    @Override
    public List<StatisticTotal> listStatisticTotal(ListStatTotalAO listStatTotalAO) {
        return listStatisticTotal(Arrays.asList(listStatTotalAO));
    }

    @Override
    public List<StatisticTotal> listStatisticTotalBatch(ListStatisticTotalBatchAO listStatisticTotalBatchAO) {
        return listStatisticTotal(listStatisticTotalBatchAO.getConditions());
    }

    private List<StatisticTotal> listStatisticTotal(List<ListStatTotalAO> listStatTotalAOS) {
        List<StatisticTotal> result = new ArrayList<>();
        List<Object> cacheResult = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            //StringRedisConnection conn = (StringRedisConnection) connection;
            listStatTotalAOS.forEach(listStatTotalAO -> {
                listStatTotalAO.getStatisticObjectIds().forEach(item -> {
                    StatisticTotal statisticTotal = StatisticTotal.builder().startDate(listStatTotalAO.getDate())
                            .statisticObjectClassEnum(listStatTotalAO.getStatisticObjectClassEnum()).statisticObjectId(item)
                            .statisticPeriodEnum(listStatTotalAO.getStatisticPeriod()).statTotalItemNameEnum(listStatTotalAO.getStatTotalItemNameEnum()).startDate(listStatTotalAO.getDate()).build();
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
                if (value instanceof Double){
                    result.get(i).setDoubleValue((Double)value);
                }
                else if (value instanceof Integer){
                    result.get(i).setLongValue(((Integer)value).longValue());
                }
                prepareResult.add(result.get(i));
            }
        }
        return prepareResult;
    }
    private Map<String, String> hashGetMultiKeyResult(List<String> result, List<String> keys) {
        Map<String, String> ret = result == null ? new HashMap<>() : new HashMap<>(result.size());
        if (result != null) {
            int size = keys.size();
            for (int i = 0; i < size; ++i) {
                String value = result.get(i);
                String key = keys.get(i);
                if (value != null)
                    ret.put(key, value);
                else
                    ret.put(key, null);
            }
        }
        return ret;
    }
}
