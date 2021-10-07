package com.x.provider.general.enums;


import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.statistic.enums.StatTotalItemNameEnum;
import com.x.provider.api.statistic.enums.StatisticObjectClassEnum;
import com.x.provider.api.statistic.enums.StatisticPeriodEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum StarStatisticEnum {

    VIDEO_STAR_TOTAL(StarItemTypeEnum.VIDEO, StatisticObjectClassEnum.VIDEO, StatisticPeriodEnum.ALL, StatTotalItemNameEnum.VIDEO_STAR_COUNT),
    COMMENT_STAR_TOTAL(StarItemTypeEnum.COMMENT, StatisticObjectClassEnum.COMMENT, StatisticPeriodEnum.ALL, StatTotalItemNameEnum.VIDEO_COMMENT_COUNT),
    ;
    private StarItemTypeEnum starItemType;
    private StatisticObjectClassEnum statisticObjectClass;
    private StatisticPeriodEnum statisticPeriod;
    private StatTotalItemNameEnum statTotalItemName;

    StarStatisticEnum(StarItemTypeEnum starItemType, StatisticObjectClassEnum statisticObjectClass, StatisticPeriodEnum statisticPeriod, StatTotalItemNameEnum statTotalItemName){
        this.statisticObjectClass = statisticObjectClass;
        this.statisticPeriod = statisticPeriod;
        this.statTotalItemName = statTotalItemName;
        this.starItemType = starItemType;
    }

    public static List<StarStatisticEnum> valueOf(int starItemType){
        return Arrays.stream(StarStatisticEnum.values()).filter(item -> item.starItemType.getValue().equals(starItemType)).collect(Collectors.toList());
    }

    public StarItemTypeEnum getStarItemType() {
        return starItemType;
    }

    public StatisticObjectClassEnum getStatisticObjectClass() {
        return statisticObjectClass;
    }

    public StatisticPeriodEnum getStatisticPeriod() {
        return statisticPeriod;
    }

    public StatTotalItemNameEnum getStatTotalItemName() {
        return statTotalItemName;
    }
}
