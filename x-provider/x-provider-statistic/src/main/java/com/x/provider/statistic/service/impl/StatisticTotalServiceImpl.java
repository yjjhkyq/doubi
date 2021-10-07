package com.x.provider.statistic.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.core.utils.BeanUtil;
import com.x.provider.api.statistic.model.ao.ListStatisticTotalBatchAO;
import com.x.provider.api.statistic.model.ao.ListStatTotalAO;
import com.x.provider.api.statistic.model.dto.ListStatisticTotalMapDTO;
import com.x.provider.api.statistic.model.event.StatisticTotalEvent;
import com.x.provider.statistic.model.domain.StatisticTotal;
import com.x.provider.statistic.service.RedisKeyService;
import com.x.provider.statistic.service.StatisticTotalKeyService;
import com.x.provider.statistic.service.StatisticTotalService;
import com.x.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatisticTotalServiceImpl implements StatisticTotalService {

    private final RedisKeyService redisKeyService;
    private final RedisService redisService;
    private final StatisticTotalKeyService statisticTotalKeyService;
    private final RedisTemplate redisTemplate;

    public StatisticTotalServiceImpl(RedisKeyService redisKeyService,
                                     RedisService redisService,
                                     StatisticTotalKeyService statisticTotalKeyService,
                                     RedisTemplate redisTemplate){
        this.redisKeyService = redisKeyService;
        this.redisService = redisService;
        this.statisticTotalKeyService = statisticTotalKeyService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onStatTotal(StatisticTotalEvent statisticTotalEvent) {
        incStatTotal(BeanUtil.prepare(statisticTotalEvent, StatisticTotal.class));
    }

    public void incStatTotal(StatisticTotal statisticTotal){
        Pair<String, String> keyFieldPair = statisticTotalKeyService.packageKey(statisticTotal);
        String redisKey = redisKeyService.getStatisticTotalKey(keyFieldPair.getFirst());
        if (statisticTotal.getLongValue() != null && statisticTotal.getLongValue() >0) {
            redisService.incr(redisKey, keyFieldPair.getSecond(), statisticTotal.getLongValue());
        }
        else if (statisticTotal.getDoubleValue() != null){
            redisService.incr(redisKey, keyFieldPair.getSecond(), statisticTotal.getDoubleValue());
        }
        else {
            log.error("no any value find for stat total, stat total item name:{}, statistic period:{}", statisticTotal.getStatTotalItemNameEnum(), statisticTotal.getStatisticPeriodEnum());
            throw new IllegalStateException(StrUtil.format("no any value find for stat total, stat total item name:{}, statistic period:{}", statisticTotal.getStatTotalItemNameEnum(), statisticTotal.getStatisticPeriodEnum()));
        }
    }

    @Override
    public ListStatisticTotalMapDTO listStatisticTotalMap(ListStatisticTotalBatchAO listStatisticTotalAO) {
        return null;
    }

    public List<StatisticTotal> listStatisticTotal(ListStatTotalAO listStatTotalAO) {
        List<StatisticTotal> result = new ArrayList<>();
        List<Object> cacheResult = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            //StringRedisConnection conn = (StringRedisConnection) connection;
            listStatTotalAO.getStatisticObjectIds().forEach(item -> {
                StatisticTotal statisticTotal = StatisticTotal.builder().startDate(listStatTotalAO.getDate())
                        .statisticObjectClassEnum(listStatTotalAO.getStatisticObjectClassEnum()).statisticObjectId(item)
                        .statisticPeriodEnum(listStatTotalAO.getStatisticPeriod()).statTotalItemNameEnum(listStatTotalAO.getStatTotalItemNameEnum()).build();
                result.add(statisticTotal);
                Pair<String, String> keyField = statisticTotalKeyService.packageKey(statisticTotal);
                connection.hMGet(redisKeyService.getStatisticTotalKey(keyField.getFirst()).getBytes(), keyField.getSecond().getBytes());
            });
            return null;
        });
        prepare(cacheResult, result);
        return result;
    }

    private void prepare(List<Object> values, List<StatisticTotal> result){
        if (values != null){
            for (int i = 0, j = values.size(); i < j; i ++){
                if (values == null || CollectionUtils.isEmpty((List)values.get(i))){
                    result.get(i).setLongValue(0L);
                    result.get(i).setDoubleValue(0.0);
                    continue;
                }
                Object value = ((List)values.get(i)).get(0);
                if (value instanceof Double){
                    result.get(i).setDoubleValue((Double)value);
                }
                else if (value instanceof Integer){
                    result.get(i).setLongValue(((Integer)value).longValue());
                }
            }
        }
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
