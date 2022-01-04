package com.x.provider.statistic.enums;

import com.x.provider.api.common.enums.ItemTypeEnum;
import com.x.provider.api.statistic.enums.MetricEnum;
import com.x.provider.api.statistic.enums.PeriodEnum;

public enum  StatisticTotalChangedNotifyItemEnum {
    VIDEO_STAR_COUNT_TOTAL(MetricEnum.SCORE.getValue(), PeriodEnum.ALL.getValue(), ItemTypeEnum.VIDEO.getValue()),
    ;

    private Integer periodEnum;
    private Integer metricEnum;
    private Integer itemTypeEnum;

    StatisticTotalChangedNotifyItemEnum(Integer metricEnum, Integer periodEnum, Integer itemTypeEnum){
        this.itemTypeEnum = itemTypeEnum;
        this.periodEnum = periodEnum;
        this.metricEnum = metricEnum;
    }

    public  static StatisticTotalChangedNotifyItemEnum valeOf(Integer metricEnum, Integer periodEnum, Integer itemTypeEnum){
        for (StatisticTotalChangedNotifyItemEnum item : StatisticTotalChangedNotifyItemEnum.values()) {
            if (item.metricEnum.equals(metricEnum) && item.periodEnum.equals(periodEnum) && item.itemTypeEnum.equals(itemTypeEnum)){
                return item;
            }
        }
        return null;
    }

}
