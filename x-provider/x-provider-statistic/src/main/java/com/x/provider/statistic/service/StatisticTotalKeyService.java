package com.x.provider.statistic.service;

import com.x.provider.statistic.model.domain.StatisticTotal;
import org.springframework.data.util.Pair;

public interface StatisticTotalKeyService {
    Pair<String, String> packageKey(StatisticTotal statisticTotal);
    StatisticTotal parseKey(String key, String field);
}
