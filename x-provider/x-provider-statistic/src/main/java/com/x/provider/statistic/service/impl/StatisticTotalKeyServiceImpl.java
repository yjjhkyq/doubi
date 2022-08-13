package com.x.provider.statistic.service.impl;

import cn.hutool.core.util.StrUtil;
import com.x.core.utils.DateUtils;
import com.x.provider.api.statistic.enums.PeriodEnum;
import com.x.provider.statistic.model.domain.StatisticTotal;
import com.x.provider.statistic.service.StatisticTotalKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatisticTotalKeyServiceImpl implements StatisticTotalKeyService {

    private final static String KEY_SPLITTER = ":";
    private final static String KEY = "{}" + KEY_SPLITTER + "{}";
    private final static String FIELD = "{}" + KEY_SPLITTER + "{}" + KEY_SPLITTER + "{}";
    private final static String DATE_KEY = DateUtils.YYYYMMDDHHMMSS;
    @Override
    public Pair<String, String> packageKey(StatisticTotal statisticTotalKeyBO) {
        return Pair.of(StrUtil.format(KEY, statisticTotalKeyBO.getItemType(), statisticTotalKeyBO.getItemId()),
                StrUtil.format(FIELD, statisticTotalKeyBO.getMetric(), statisticTotalKeyBO.getPeriod(), getDateKey(statisticTotalKeyBO)));
    }

    @Override
    public StatisticTotal parseKey(String key, String field) {
        String[] split = key.split(KEY);
        StatisticTotal statisticTotalKeyBO = new StatisticTotal();
        int index = split.length - 1;
        statisticTotalKeyBO.setItemId(split[index--]);
        statisticTotalKeyBO.setItemType(Integer.parseInt(split[index--]));
        String[] splitField = field.split(field);
        index = 0;
        statisticTotalKeyBO.setMetric(Integer.parseInt(splitField[index++]));
        statisticTotalKeyBO.setPeriod(Integer.parseInt(splitField[index++]));
        statisticTotalKeyBO.setStartDate(DateUtils.dateTime(DATE_KEY, splitField[index++]));
        return statisticTotalKeyBO;
    }

    private String getDateKey(StatisticTotal statisticTotalKeyBO){
        PeriodEnum statisticPeriodEnum = PeriodEnum.valueOf(statisticTotalKeyBO.getPeriod());
        switch (statisticPeriodEnum){
            case ALL:
                return DateUtils.parseDateToStr(DATE_KEY, DateUtils.minDate());
            default:
                throw new IllegalStateException("not supported for statistic period:" + statisticPeriodEnum.name());
        }
    }
}
