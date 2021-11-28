package com.x.provider.api.mc.model.protocol;

/**
 * 消息类型
 */
public enum MessageType {
    /**
     * 没有消息内容
     */
    NONE,
    /**
     * 文本消息
     */
    TEXT,
    /**
     * 图片
     */
    IMAGE,
    /**
     * 视频
     */
    VIDEO,
    CARD,
    /**
     * 关注列表
     */
    FOLLOW,
    /**
     * 通用消息
     */
    GENERAL_MSG,
    /**
     * 点赞视频
     */
    STAR_VIDEO,
    /**
     * 点赞视频评论
     */
    STAR_VIDEO_COMMENT,
    /**
     * 视频评论或者回复视频评论
     */
    COMMENT_VIDEO
}
