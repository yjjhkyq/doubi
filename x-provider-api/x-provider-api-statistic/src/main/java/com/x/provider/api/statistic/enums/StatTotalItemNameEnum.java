package com.x.provider.api.statistic.enums;

public enum StatTotalItemNameEnum {
    VIDEO_STAR_COUNT(1),
    VIDEO_PLAY_COUNT(2),
    VIDEO_COMMENT_COUNT(3),
    VIDEO_COMMENT_STAR_COUNT(4),
    VIDEO_COMMENT_REPLY_COUNT(5)
    ;

    private final int value;

    StatTotalItemNameEnum(int value){
        this.value = value;
    }

    public static StatTotalItemNameEnum valueOf(int value){
        for (StatTotalItemNameEnum item : StatTotalItemNameEnum.values()) {
            if (item.value == value){
                return item;
            }
        }
        throw new IllegalStateException("value:" + value);
    }

    public int getValue(){
        return value;
    }
}
