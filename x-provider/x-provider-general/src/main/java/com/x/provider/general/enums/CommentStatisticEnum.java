package com.x.provider.general.enums;


import com.x.provider.api.general.enums.CommentItemTypeEnum;
import com.x.provider.api.general.enums.StarItemTypeEnum;
import com.x.provider.api.statistic.enums.StatTotalItemNameEnum;
import com.x.provider.api.statistic.enums.StatisticObjectClassEnum;
import com.x.provider.api.statistic.enums.StatisticPeriodEnum;
import io.swagger.models.auth.In;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CommentStatisticEnum {

    VIDEO_COMMENT_TOTAL(CommentItemTypeEnum.VIDEO, false, StatisticObjectClassEnum.VIDEO, StatisticPeriodEnum.ALL, StatTotalItemNameEnum.VIDEO_COMMENT_COUNT),
    VIDEO_COMMENT_REPLY_TOTAL(CommentItemTypeEnum.VIDEO, true, StatisticObjectClassEnum.COMMENT, StatisticPeriodEnum.ALL, StatTotalItemNameEnum.VIDEO_COMMENT_REPLY_COUNT),
    ;
    private Boolean reply;
    private CommentItemTypeEnum itemType;
    private StatisticObjectClassEnum statisticObjectClass;
    private StatisticPeriodEnum statisticPeriod;
    private StatTotalItemNameEnum statTotalItemName;

    CommentStatisticEnum(CommentItemTypeEnum itemType, Boolean reply, StatisticObjectClassEnum statisticObjectClass, StatisticPeriodEnum statisticPeriod, StatTotalItemNameEnum statTotalItemName){
        this.statisticObjectClass = statisticObjectClass;
        this.statisticPeriod = statisticPeriod;
        this.statTotalItemName = statTotalItemName;
        this.itemType = itemType;
        this.reply = reply;
    }

    public static List<CommentStatisticEnum> valueOf(Integer itemType, Boolean reply){
        return Arrays.stream(CommentStatisticEnum.values()).filter(item -> item.itemType.getValue().equals(itemType) && item.reply.equals(reply)).collect(Collectors.toList());
    }

    public CommentItemTypeEnum getItemType() {
        return itemType;
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
