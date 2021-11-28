package com.x.provider.api.mc.model.protocol;

import lombok.Data;

@Data
public class InteractMsgBody {
    /**
     * 交互来源用户id
     */
    private Long fromUid;
    /**
     * 交互来源用户昵称
     */
    private String fromNickName;
    /**
     * 交互来源用户头像
     */
    private String fromAvatarUrl;

    /**
     * 交互描述信息
     */
    private String interactiveDescription;

    /**
     * 交互目标id 暂时只有视频id
     */
    private Long interactiveTargetId;
    /**
     * 交互目标图标 暂时只有视频封面
     */
    private String interactiveIcon;

    /**
     * 提示消息
     */
    private String alertMsg;
}
