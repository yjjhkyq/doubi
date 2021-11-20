package com.x.provider.api.mc.model.ao;

import java.util.Map;

public class NotifyProtocol {

    public enum InteractiveType{
        STAR,
        COMMENT
    }

    public enum InteractiveTargetType {
        VIDEO
    }

    public static class InteractiveNotifyMsgBody {
        private Long fromUid;
        private String fromNickName;
        private String fromAvatarUrl;

        private Integer interactiveType;
        private Integer interactiveTargetType;
        private Long interactiveTargetId;
        private String interactiveIcon;
        private Map<String, Object> extra;
    }

    public static class SystemNotifyMsgBody{
        private String title;
        private String content;
    }

    public static class CommentExtra {
        private boolean reply;
        private String commentContent;
    }
}
