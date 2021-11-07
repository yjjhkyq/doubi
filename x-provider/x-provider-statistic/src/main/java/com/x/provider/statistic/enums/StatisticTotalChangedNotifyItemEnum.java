package com.x.provider.statistic.enums;

import com.x.provider.api.statistic.enums.StatTotalItemNameEnum;
import com.x.provider.api.statistic.enums.StatisticObjectClassEnum;
import com.x.provider.api.statistic.enums.StatisticPeriodEnum;

public enum  StatisticTotalChangedNotifyItemEnum {
    VIDEO_STAR_COUNT_TOTAL(StatTotalItemNameEnum.VIDEO_SCORE.getValue(), StatisticPeriodEnum.ALL.getValue(), StatisticObjectClassEnum.VIDEO.getValue()),
    ;

    private Integer statisticPeriodEnum;
    private Integer statTotalItemNameEnum;
    private Integer statisticObjectClassEnum;

    StatisticTotalChangedNotifyItemEnum(Integer statTotalItemNameEnum, Integer statisticPeriodEnum, Integer statisticObjectClassEnum){
        this.statisticObjectClassEnum = statisticObjectClassEnum;
        this.statisticPeriodEnum = statisticPeriodEnum;
        this.statTotalItemNameEnum = statTotalItemNameEnum;
    }

    public  static StatisticTotalChangedNotifyItemEnum valeOf(Integer statTotalItemNameEnum, Integer statisticPeriodEnum, Integer statisticObjectClassEnum){
        for (StatisticTotalChangedNotifyItemEnum item : StatisticTotalChangedNotifyItemEnum.values()) {
            if (item.statTotalItemNameEnum.equals(statTotalItemNameEnum) && item.statisticPeriodEnum.equals(statisticPeriodEnum) && item.statisticObjectClassEnum.equals(statisticObjectClassEnum)){
                return item;
            }
        }
        return null;
    }

}
