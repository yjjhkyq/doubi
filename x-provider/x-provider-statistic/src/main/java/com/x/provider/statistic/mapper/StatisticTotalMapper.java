package com.x.provider.statistic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.x.provider.statistic.model.bo.IncStatisticTotalBO;
import com.x.provider.statistic.model.domain.StatisticTotal;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticTotalMapper extends BaseMapper<StatisticTotal> {
    Integer incValue(IncStatisticTotalBO incStatisticTotalBO);
}
